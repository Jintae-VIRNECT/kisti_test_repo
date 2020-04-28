<template>
  <div class="card" :class="{ no__button: !showMessageButton }">
    <div class="card-center">
      <div class="card-profile--thumb" :class="{ expired: !license }">
        <profile :image="imageUrl" size="4.571rem"></profile>
        <span
          v-if="status && license"
          class="card-profile--badge"
          :class="status"
          >{{ status }}</span
        >
      </div>
      <div class="card-profile--name">
        {{ name }}
      </div>
      <div class="card-profile--email">{{ email }}</div>

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
import ProfileImage from 'ProfileImage'
import Role from 'Role'
export default {
  name: 'Card',
  components: {
    Profile: ProfileImage,
    Role,
  },

  props: {
    status: {
      type: String,
      validator: value => ['', 'online', 'busy', 'offline'].indexOf(value) >= 0,
    },
    imageUrl: {
      type: String,
      default: require('assets/image/img_user_profile.svg'),
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
        return ''
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

<style lang="scss" scoped>
@import '~assets/style/vars';
.card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 2.286rem;
  background-color: rgba($color_darkgray_500, 0.9);
  border: solid 1px #3e3e42;
  border-radius: 2px;
  &.no__button {
    padding-bottom: 1.429rem;
  }

  &:hover {
    background-color: $color_darkgray_500;
  }
}
.card-center {
  display: flex;
  flex-direction: column;
  flex-grow: 3;
  align-items: center;
  // min-height: 180px;
  margin-bottom: 0.714rem;
}

.card-profile--thumb {
  position: relative;
  padding: 0.214rem;
  border: 1.4px solid transparent;

  &.expired {
    border-color: #c51803;
    border-radius: 50%;
  }
}

.card-profile--name {
  margin-top: 1.214rem;
  margin-bottom: 0.286rem;
  font-weight: 500;
  font-size: 1.071rem;
}
.card-profile--email {
  margin-bottom: 0.786rem;
  color: #98a0a6;
  font-size: 0.929rem;
}

.card-profile--badge {
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
  margin-bottom: 1rem;
  padding-top: 1rem;
  font-size: 0.929rem;

  text-align: center;
  border-top: solid 1px rgba(#3e3e42, 0.92);
}
</style>

<style lang="scss">
.card {
  .role {
    margin: 0.571rem 0;
  }
}
</style>
