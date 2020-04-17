<template>
  <div class="card" :class="{ no__button: !showMessageButton }">
    <div class="card-center">
      <div class="card-center-profile--thumb" :class="{ expired: !license }">
        <div
          v-if="color.length > 0"
          class="card-center-profile--image"
          :style="{ 'background-color': color }"
        ></div>
        <p v-else class="card-center-profile--image">
          <img :src="imageUrl" />
        </p>
        <span
          v-if="status && license"
          class="card-center-profile--badge"
          :class="status"
          >{{ status }}</span
        >
      </div>
      <div class="card-center-profile--name" :class="{ expired: !license }">
        {{ name }}
      </div>
      <div class="card-center-profile--email">{{ email }}</div>

      <role v-if="role && license" :role="role" :opt="opt"></role>
      <role
        v-else-if="license === false"
        :role="'라이센스 만료'"
        :opt="opt"
      ></role>
    </div>
    <div v-if="showMessageButton" class="card-bottom">
      <p>메시지 보내기</p>
    </div>
  </div>
</template>

<script>
import Role from 'Role'
export default {
  name: 'Card',
  components: {
    Role,
  },

  props: {
    status: {
      type: String,
      validator: value => ['', 'online', 'busy', 'offline'].indexOf(value) >= 0,
    },
    color: {
      default: '',
    },
    imageUrl: {
      type: String,
      default: require('assets/image/img-default-user.svg'),
    },
    name: {
      type: String,
      default: '',
    },
    email: {
      type: String,
      default: '',
    },
    role: {
      type: String,
      default: '',
    },
    license: {
      type: Boolean,
      default: true,
    },
    showMessageButton: {
      type: Boolean,
      default: false,
    },
    showSignal: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  computed: {
    cardWidth() {
      if (this.width === 'full') {
        return '100%'
      } else {
        return this.width + 'px'
      }
    },
    opt() {
      if (this.license) {
        return 'card'
      } else {
        return 'expired'
      }
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/vars';
.card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 32px;

  background-color: #313135e6;
  border: solid 1px #3e3e42;
  border-radius: 2px;
  &.no__button {
    padding-bottom: 20px;
  }
  max-width: 204px;
}
.card-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-grow: 3;
  min-height: 180px;
  margin-bottom: 10px;
}

.card-center-profile--thumb {
  position: relative;
  width: 64px;
  height: 64px;

  &.expired {
    height: 73px;
    width: 73px;
    border: solid;
    border-width: 1.4px;
    border-color: red;
    border-radius: 50%;
    padding: 3px;
  }
}

.card-center-profile--image {
  width: 100%;
  height: 100%;
  overflow: hidden;
  line-height: 0;
  background-color: #fff;
  border-radius: 50%;
}

.card-center-profile--name {
  color: rgb(255, 255, 255);
  font-size: 15px;
  font-family: NotoSansCJKkr-Medium;
  font-weight: 500;
  text-align: center;
  margin-top: 17px;
  margin-bottom: 4px;
  &.expired {
    margin-top: 11px;
    margin-bottom: 3px;
  }
}
.card-center-profile--email {
  color: rgb(152, 160, 166);
  font-size: 13px;
  font-family: Roboto-Regular;
  font-weight: normal;
  text-align: center;
  margin-bottom: 11px;
}

.card-center-profile--badge {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 26%;
  height: 26%;
  overflow: hidden;
  text-indent: -99px;
  border-radius: 50%;

  &.busy {
    background-color: $color_busy;
  }
  &.online {
    background-color: $color_online;
  }
  &.offline {
    background-color: $color_offline;
  }
}

.card-bottom {
  border-top-color: rgba(62, 62, 66, 0.92);
  border-top-style: solid;
  border-top-width: 1px;
  text-align: center;
  width: 100%;

  font-size: 13px;
  font-family: NotoSansCJKkr-Regular;
  font-weight: normal;
  text-align: center;
  letter-spacing: 0px;
  flex-grow: 1;
  padding-top: 14px;
  margin-bottom: 14px;
  > p {
    color: rgb(255, 255, 255);
  }
}
</style>
