<template>
  <div class="card" :class="{ no__button: !showMessageButton }" width="full">
    <div class="card-center">
      <div class="card-center-profile--thumb" :class="{ expired: !license }">
        <div
          v-if="color.length > 0"
          class="card-center-profile--image"
          :style="{ 'background-color': color }"
        ></div>
        <p v-else class="card-center-profile--image">
          <img :src="imageUrl" @error="onImageError" />
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
  background-color: rgba(49, 49, 53, 0.902);
  border: solid 1px #3e3e42;
  border-radius: 2px;
  &.no__button {
    padding-bottom: 20px;
  }
  .role {
    margin: 8px 0px 8px 0px;
  }

  &:hover {
    background-color: #313135;
  }
}
.card-center {
  display: flex;
  flex-direction: column;
  flex-grow: 3;
  align-items: center;
  min-height: 180px;
  margin-bottom: 10px;
}

.card-center-profile--thumb {
  position: relative;
  width: 64px;
  height: 64px;

  &.expired {
    width: 73px;
    height: 73px;
    padding: 3px;
    border: solid;
    border-color: red;
    border-width: 1.4px;
    border-radius: 50%;
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
  margin-top: 17px;
  margin-bottom: 4px;
  color: #ffffff;
  font-weight: 500;
  font-size: 15px;
  text-align: center;
  &.expired {
    margin-top: 11px;
    margin-bottom: 3px;
  }
}
.card-center-profile--email {
  margin-bottom: 11px;
  color: rgb(152, 160, 166);
  font-size: 13px;
  text-align: center;
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
  flex-grow: 1;
  width: 100%;
  margin-bottom: 14px;
  padding-top: 14px;
  font-size: 13px;

  text-align: center;
  border-top-color: rgba(62, 62, 66, 0.92);
  border-top-width: 1px;
  border-top-style: solid;
  > p {
    color: rgb(255, 255, 255);
  }
}
</style>
