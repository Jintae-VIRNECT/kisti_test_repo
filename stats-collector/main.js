const kurento = require("kurento-client");
const dayjs = require("dayjs");
const Influx = require("influx");

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

const env = process.env.NODE_ENV;
const publicIp = process.env.PUBLIC_IP;
const influxUrl =
  process.env.INFLUXDB_URL || "http://admin:admin@localhost:8086/db0";
const wsUri = process.argv[2] || "ws://localhost:8888/kurento";
const interval = 10 * 1000;
let kurentoClient = null;
const influx = new Influx.InfluxDB(influxUrl);

async function getKurentoClient() {
  try {
    const _kurentoClient = await kurento(wsUri);
    return _kurentoClient;
  } catch (err) {
    console.log(`Could not find media server at address(${wsUri}) err(${err})`);
    return;
  }
}

async function getStats(server) {
  if (!server) {
    return "error - failed to find server";
  }

  const pipelines = await server.getPipelines();
  const elementsPromises = pipelines.map(async (p) => {
    await p.setLatencyStats(true);
    return await new Promise(async (resolve, reject) => {
      try {
        const e = await p.getChildren();
        resolve(
          e.map((elem) => {
            elem.pipelineId = p.id;
            return elem;
          })
        );
      } catch (err) {
        console.error(err);
        reject();
      }
    });
  });

  const elements = [].concat.apply([], await Promise.all(elementsPromises));
  const stats = Object.values(elements)
    .filter((e) => e.id.indexOf("WebRtcEndpoint") > 0)
    .map((e) => getAudioVideoStat(e));

  return (await Promise.all(stats))
    .map(Object.values)
    .reduce((acc, e) => [...acc, ...e], []);
}

async function getAudioVideoStat(element) {
  const statPromises = ["VIDEO", "AUDIO"].map((mediaType) => {
    return new Promise(async (resolve, reject) => {
      const stat = await element.getStats(mediaType);
      const elems = Object.values(stat).map((elem) => ({
        ...elem,
        pipelineId: element.pipelineId,
        mediaType,
      }));
      resolve(elems);
    });
  });

  return [].concat.apply([], await Promise.all(statPromises));
}

async function start() {
  try {
    const _kurentoClient = await getKurentoClient();
    const mediaServer = await _kurentoClient.getServerManager();

    setInterval(async () => {
      try {
        const stats = await getStats(mediaServer);
        stats.forEach(async (s) => {
          if (s.type === "endpoint") {
            delete s.inputLatency;
            delete s.E2ELatency;
          }

          const fields = {
            ...s,
            env,
            publicIp,
            time: dayjs().toISOString(),
          };

          await influx.writePoints([
            {
              measurement: fields.type == "endpoint" ? "endpoint" : "rtp",
              tags: {
                mediaType: fields.mediaType,
                type: fields.type,
                env: fields.env,
                publicIp: fields.publicIp,
              },
              fields,
            },
          ]);
        });
      } catch (err) {
        console.warn(err);
      }
    }, interval);
  } catch (err) {
    console.log("Failed load kurento client. " + err);
    exit(1);
  }
}

function exit(code) {
  process.exit(code);
}

function stop() {
  if (kurentoClient) {
    kurentoClient.close();
  }
  exit(0);
}

process.on("SIGINT", stop);

process.on("unhandledRejection", (error) => {
  console.log("unhandledRejection", error.message);
});

start();
