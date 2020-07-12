// use thee.js
// url: https://discoverthreejs.com/tips-and-tricks/

const chunks = [];
var renderer;

var video = document.querySelector("video");
video.addEventListener(
  "play",
  () => {
    console.log("play video");

    threeRender(video);

    var stream = renderer.domElement.captureStream(30);
    console.log(stream);

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

function threeRender(video) {
  var scene = new THREE.Scene();

  var camera = new THREE.PerspectiveCamera(75, 1280 / 720, 99, 100);
  renderer = new THREE.WebGLRenderer({ powerPreference: "high-performance" });
  renderer.setSize(1280, 720);
  // document.body.appendChild(renderer.domElement);

  // load a texture, set wrap mode to repeat
  var texture = new THREE.Texture(video);
  var geometry = new THREE.PlaneGeometry(272, 153);
  var material = new THREE.MeshBasicMaterial({
    map: texture,
  });
  var plane = new THREE.Mesh(geometry, material);
  scene.add(plane);

  camera.position.z = 100;

  //render the scene
  function render() {
    requestAnimationFrame(render);
    texture.needsUpdate = true;
    renderer.render(scene, camera);
  }

  render();
}
