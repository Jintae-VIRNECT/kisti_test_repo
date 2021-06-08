<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    width="60rem"
    height="43.3571rem"
    :beforeClose="beforeClose"
    class="modal-position-map"
    :title="'위치 정보'"
  >
    <div class="modal-position-map__body">
      <div v-if="state === 'requesting'" class="modal-position-map__request">
        <p class="modal-position-map__request">
          위치 정보를 요청 중 입니다.
        </p>
      </div>
      <div v-else-if="state === 'showing'" class="modal-position-map__showing">
        <div class="modal-position-map__showing--map" id="map"></div>
        <button @click="refresh" class="modal-position-map__showing--refresh">
          위치 새로고침
        </button>
      </div>
      <div v-else-if="state === 'gpsOff'" class="modal-position-map__gpsoff">
        <p class="modal-position-map__gpsoff--title">
          GPS 정보를 확인할 수 없습니다.
        </p>
        <p class="modal-position-map__gpsoff--description">
          요청한 참가자에게 GPS 기능 사용여부를 확인해 주세요.
        </p>
        <button
          @click="beforeClose"
          class="modal-position-map__gpsoff--close btn"
          :class="{ refreshing: isRefreshing }"
        >
          닫기
        </button>
      </div>
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
import { mapGetters } from 'vuex'
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
      state: null, //requesting, showing, gpsOff
      visibleFlag: false,

      location: null, //{lat, lng}
      map: null,
      marker: null,
      isRefreshing: false,
    }
  },

  computed: {
    ...mapGetters(['mainView']),
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
      if (flag) {
        this.showMap()
      }
    },
    mainView: {
      handler(oldView, newView) {
        if (
          oldView.connectionId !== undefined &&
          oldView.connectionId !== newView.connectionId
        ) {
          this.beforeClose()
        }
      },
      deep: true,
    },
  },
  methods: {
    beforeClose() {
      this.location = null
      if (this.mainView) {
        this.$call.sendStopLocation([this.mainView.connectionId])
      }
      this.$eventBus.$emit('map:closed')
      this.$emit('update:visible', false)
    },
    updatePosition(location) {
      this.location = location
      if (this.map) {
        this.map.setCenter(this.location)
        this.marker.setPosition(this.location)
        this.isRefreshing = false
      }
    },
    showGpsOff() {
      this.state = 'gpsOff'
    },
    async showMap() {
      this.state = 'requesting'

      const options = {
        version: 'weekly',
      }
      const loader = new Loader(GOOGLE_MAP_API, options)

      //수신된 좌표가 없으면 status  requesting

      //defalut 대한민국
      let lat = 37.525061051135225
      let lng = 126.96228142617416

      if (this.location) {
        lat = this.location.lat
        lng = this.location.lng
        this.state = 'showing'
      }

      const google = await loader.load()
      this.map = new google.maps.Map(document.getElementById('map'), {
        center: { lat, lng },
        zoom: 1,
        disableDefaultUI: true,
      })

      this.map.setCenter({ lat, lng })

      const pos = new google.maps.LatLng(lat, lng)
      this.marker = new google.maps.Marker({
        position: pos,
      })

      this.marker.setMap(this.map)
    },
    refresh() {
      this.$call.sendRequestLocation([this.mainView.connectionId])
      this.isRefreshing = true
    },
  },

  mounted() {
    this.$eventBus.$on('map:location', this.updatePosition)
    this.$eventBus.$on('map:gpsoff', this.showGpsOff)
    this.$eventBus.$on('map:close', this.beforeClose)
  },
  beforeDestroy() {
    this.$eventBus.$off('map:location', this.updatePosition)
    this.$eventBus.$off('map:gpsoff', this.showGpsOff)
    this.$eventBus.$off('map:close', this.beforeClose)
  },
}
</script>
<style lang="scss">
.modal-position-map__body {
  width: 100%;
  height: 100%;
  background-color: #29292c;

  & > div {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
  }
}

.modal-position-map__request {
  & > p {
    color: #dedede;
    font-weight: normal;
    font-size: 16px;
  }
}
.modal-position-map__showing {
  .modal-position-map__showing--map {
    width: 100%;
    height: 100%;
  }
  .modal-position-map__showing--refresh {
    position: absolute;
    bottom: 30px;
    width: 136px;
    height: 44px;
    color: #dedede;
    font-weight: normal;
    font-size: 14px;
    background: #000000;
    border-radius: 24px;
    opacity: 0.9;

    &.refreshing {
      color: transparent;
      &:after {
        position: absolute;
        top: 50%;
        left: 50%;
        width: 2.857rem;
        height: 2.857rem;
        background: center center/2.857rem 2.857rem no-repeat
          url(~assets/image/workspace/loading.gif);
        transform: translate(-50%, -50%);
        content: '';
      }
    }
  }
}

.modal-position-map__gpsoff {
  .modal-position-map__gpsoff--title {
    color: rgb(250, 250, 250);
    font-weight: 500;
    font-size: 24px;
  }

  .modal-position-map__gpsoff--description {
    color: rgb(250, 250, 250);
    font-weight: normal;
    font-size: 18px;
    opacity: 0.5;
  }
  .modal-position-map__gpsoff--close {
    width: 120px;
    height: 38px;
    margin-top: 30px;
    padding: 0;
    border-radius: 4px;
  }
}
</style>
