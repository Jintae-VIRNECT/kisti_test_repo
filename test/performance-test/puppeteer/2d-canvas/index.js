const chunks = [];

var video = document.querySelector("video");
var canvas = document.createElement("canvas");
// var canvas = document.querySelector("canvas");
var ctx = canvas.getContext("2d");

video.addEventListener(
  "play",
  () => {
    console.log("play video");
    canvas.width = 1280;
    canvas.height = 720;

    var stream = canvas.captureStream(30);
    console.log(stream);
    draw();
    var options = {
      audioBitsPerSecond: 128000,
      videoBitsPerSecond: 1500000,
      mimeType: 'video/webm;codecs="vp8"'
    }
    var recorder = new MediaRecorder(stream, options);
    recorder.ondataavailable = function (e) {
      console.log(e.data);
      chunks.push(e.data);
    };
    recorder.onstop = () => {
      console.log("stop recording");
      download();
    };

    console.log("start recording");
    recorder.start(3 * 1000);

    setTimeout(() => {
      recorder.stop();
      video.pause();
    }, 60 * 1000);
  },
  false
);

function draw() {
  ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
  requestAnimationFrame(draw);
}

function download() {
  var blob = new Blob(chunks, {
    type: "video/webm",
  });
  var url = URL.createObjectURL(blob);
  var a = document.createElement("a");
  document.body.appendChild(a);
  a.style = "display: none";
  a.href = url;
  a.download = "test.webm";
  a.click();
  window.URL.revokeObjectURL(url);
}
