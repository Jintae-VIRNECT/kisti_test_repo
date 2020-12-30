const chunks = [];

var video = document.querySelector("video");

video.addEventListener(
  "play",
  () => {
    console.log("play video");

    var stream = video.captureStream(30);
    console.log(stream);

    var recorder = new MediaRecorder(stream, { mimeType: "video/webm" });
    recorder.ondataavailable = function (e) {
      console.log(e.data);
      chunks.push(e.data);
    };

    console.log("start recording");
    recorder.start(3 * 1000);

    setTimeout(() => {
      console.log("stop recording");
      recorder.stop();
      video.pause();
      download();
    }, 60 * 1000);
  },
  false
);

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
