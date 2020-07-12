const puppeteer = require("puppeteer-core");
const path = require('path');
const cluster = require('cluster');

const args = process.argv.slice(2);
const testCase = args[0];
const numWokers = args[1];

if (cluster.isMaster) {
  for (let i = 0; i < numWokers; i++) {
    cluster.fork();
  }
} else {
  (async () => {
    const browser = await puppeteer.launch({
      executablePath: path.normalize("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"),
      dumpio: false,
      headless: false,
      args: [
        "--headless",
        // "--kiosk",
        // "--start-maximized",
        "--test-type",
        "--no-sandbox",
        "--disable-infobars",
        "--disable-popup-blocking",
        "--window-position=0,0",
        "--no-first-run",
        "--ignore-certificate-errors",
        "--autoplay-policy=no-user-gesture-required",
        // "--enable-webgl",
        // "--ignore-gpu-blacklist",
        // "--skip-gpu-data-loading",
        // "--disable-dev-shm-usage",
        // "--use-gl=swiftshader-webgl",
        // "--use-gl=swiftshader",
        // "--use-gl=osmesa",
      ],
    });

    const page = await browser.newPage();

    // Test for webgl support
    const webgl = await page.evaluate(() => {
      const canvas = document.createElement("canvas");
      const gl = canvas.getContext("webgl");
      const expGl = canvas.getContext("experimental-webgl");

      return {
        gl: gl && gl instanceof WebGLRenderingContext,
        expGl: expGl && expGl instanceof WebGLRenderingContext,
      };
    });

    console.log("WebGL Support:", webgl);

    await page.goto(`http://192.168.13.51:9000/${testCase}/index.html`);

    await page._client.send("Page.setDownloadBehavior", {
      behavior: "allow",
      downloadPath: path.normalize(`D:\\recordings\\recording-${cluster.worker.id}`)
    });

    await page.waitFor(80 * 1000);

    await browser.close();

    // process.exit();
  })();
}

