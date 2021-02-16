<template>
  <div class="main-body screen">
    <screen-stop-button></screen-stop-button>
    <menus></menus>
    <main-video></main-video>
  </div>
</template>

<script>
import Menus from './tools/Menus'
import MainVideo from './stream/MainVideo'
import ScreenStopButton from './screen/ScreenStopButton'
export default {
  name: 'ServiceScreen',
  components: {
    Menus,
    MainVideo,
    ScreenStopButton,
  },
}
</script>
