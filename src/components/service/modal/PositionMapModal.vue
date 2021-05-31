<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    width="60rem"
    height="43.3571rem"
    :beforeClose="beforeClose"
    class="modal-position-map"
    :title="'현재위치?'"
  >
    <div id="map" class="modal-position-map__body">
      map
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'

import toastMixin from 'mixins/toast'
// import { Loader } from '@googlemaps/js-api-loader'
import { Loader, LoaderOptions } from 'google-maps'
// import { mapGetters, mapActions } from 'vuex'
// import { ROLE } from 'configs/remote.config'

export default {
  name: 'PositionMapModal',
  mixins: [toastMixin],
  components: {
    Modal,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      visibleFlag: false,

      tabview: '',
    }
  },

  computed: {
    // ...mapGetters([
    //   'modalSetting',
    //   'useRecording',
    //   'useLocalRecording',
    //   'useTranslate',
    //   'allowLocalRecord',
    // ]),
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
      if (flag) {
        this.showMap()
      }
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
      this.$eventBus.$emit('close:positionmap')
    },
    async showMap() {
      const options = {
        version: 'weekly',
        /* todo */
      }
      const loader = new Loader(GOOGLE_MAP_API, options)

      navigator.geolocation.getCurrentPosition(async position => {
        const lat = position.coords.latitude
        const lng = position.coords.longitude
        const google = await loader.load()
        const map = new google.maps.Map(document.getElementById('map'), {
          // center: { lat: -34.397, lng: 150.644 },
          center: { lat, lng },
          zoom: 17,
          disableDefaultUI: true,
        })

        const myLatlng = new google.maps.LatLng(37.5165442, 126.9591712)
        const marker = new google.maps.Marker({
          position: myLatlng,
          title: 'Hello World!',

          icon: require('assets/image/pengsu.png'),
        })
        marker.setMap(map)

        console.log(map)
      })
    },
  },

  async mounted() {
    // const loader = new Loader({
    //   apiKey: 'AIzaSyD0JClrnwr2SpYViHpY69M6_euI7GyUpu8',
    //   version: 'weekly',
    //   // ...additionalOptions,
    // })
    // // let recaptchaScript = document.createElement('script')
    // // recaptchaScript.setAttribute(
    // //   'src',
    // //   'https://www.google.com/recaptcha/api.js',
    // // )
    // // document.head.appendChild(recaptchaScript)
    // loader.load().then(google => {
    //   const map = new google.maps.Map(document.getElementById('map'), {
    //     center: { lat: -34.397, lng: 150.644 },
    //     zoom: 8,
    //   })
    // })
  },
  beforeDestroy() {},
}
</script>
<style lang="scss">
.modal-position-map {
  .modal-position-map__body {
    width: 100%;
    height: 100%;
  }
}
</style>
