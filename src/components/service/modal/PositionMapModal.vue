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
          {{ $t('service.map_request_position') }}
        </p>
      </div>
      <div v-else-if="state === 'showing'" class="modal-position-map__showing">
        <div class="modal-position-map__showing--map" id="map"></div>
        <button
          @click="refresh"
          class="modal-position-map__showing--refresh"
          :class="{ refreshing: isRefreshing }"
        >
          {{ $t('service.map_refresh') }}
        </button>
      </div>
      <div v-else-if="state === 'gpsOff'" class="modal-position-map__gpsoff">
        <p class="modal-position-map__gpsoff--title">
          {{ $t('service.map_cannot_check_gps') }}
        </p>
        <p class="modal-position-map__gpsoff--description">
          {{ $t('service.map_check_use_gps_description') }}
        </p>
        <button
          @click="beforeClose"
          class="modal-position-map__gpsoff--close btn"
        >
          {{ $t('common.close') }}
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
      state: 'requesting', //requesting, showing, gpsOff
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
        this.initStatus()
      } else {
        this.map = null
        this.marker = null
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
      this.state = 'showing'
      if (this.map) {
        this.map.setCenter(this.location)
        this.marker.setMap(this.map)
        this.marker.setPosition(this.location)
      } else {
        this.initMap()
      }
      this.isRefreshing = false
    },
    showGpsOff() {
      this.state = 'gpsOff'
    },
    async initStatus() {
      this.state = 'requesting'
    },
    async initMap() {
      const options = {
        version: 'weekly',
      }

      const { lat, lng } = this.location

      const loader = new Loader(GOOGLE_MAP_API, options)
      const google = await loader.load()

      this.map = new google.maps.Map(document.getElementById('map'), {
        center: { lat, lng },
        zoom: 17,
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
      this.$call.sendRequestLocation(true, [this.mainView.connectionId])
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
    font-size: 1.1429rem;
  }
}
.modal-position-map__showing {
  .modal-position-map__showing--map {
    width: 100%;
    height: 100%;
  }
  .modal-position-map__showing--refresh {
    position: absolute;
    bottom: 2.1429rem;
    width: 9.7143rem;
    height: 3.1429rem;
    color: #dedede;
    font-weight: normal;
    font-size: 1rem;
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
    color: #fafafa;
    font-weight: 500;
    font-size: 1.7143rem;
  }

  .modal-position-map__gpsoff--description {
    color: #fafafa;
    font-weight: normal;
    font-size: 1.2857rem;
    opacity: 0.5;
  }
  .modal-position-map__gpsoff--close {
    width: 8.5714rem;
    height: 2.7143rem;
    margin-top: 2.1429rem;
    padding: 0;
    border-radius: 4px;
  }
}
</style>
