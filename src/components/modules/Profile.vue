<template>
  <figure class="profile" :class="status" v-on="{ ...$listeners }">
    <div class="profile--thumb" :style="thumbStyle">
      <div class="profile--image" :class="{ group: group, image: useImage }">
        <img
          v-if="useImage"
          :src="imageUrl"
          :alt="mainText"
          @error="onImageError"
        />
      </div>
      <div v-if="status" class="profile--badge">
        <div class="profile--badge__core" :class="[status, { me: isMe }]"></div>
      </div>
    </div>
    <figcaption class="profile--text" v-if="mainText && mainText.length > 0">
      <p class="profile--maintext">{{ mainText }}</p>
      <p class="profile--subtext" v-if="subText">{{ subText }}</p>
    </figcaption>
    <role v-if="showRole" :role="role">{{ role }}</role>
  </figure>
</template>

<script>
import Role from 'Role'
import { WORKSPACE_ROLE } from 'configs/status.config'
import { ROLE } from 'configs/remote.config'
import { proxyUrl } from 'utils/file'
export default {
  name: 'Profile',
  components: {
    Role,
  },
  props: {
    thumbStyle: {
      type: Object,
      default: () => {
        return {}
      },
    },
    image: String,
    onError: {
      type: Function,
      default: null,
    },
    mainText: {
      type: String,
      default: '',
    },
    subText: String,
    status: {
      type: String,
      validator: value =>
        ['', 'login', 'collabo', 'logout'].indexOf(value) >= 0,
    },
    role: {
      type: String,
      default: null,
    },
    group: {
      type: Boolean,
      default: false,
    },
    isMe: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    showRole() {
      return (
        this.role === WORKSPACE_ROLE.MASTER ||
        this.role === WORKSPACE_ROLE.MANAGER ||
        this.role === ROLE.LEADER
      )
    },
    useImage() {
      if (this.image && this.image.length > 0 && this.image !== 'default') {
        return this.image
      } else {
        return false
      }
    },
    imageUrl() {
      return proxyUrl(this.image)
    },
  },
  watch: {
    image() {
      const img = this.$el.querySelector('img')
      if (img && img.style.display === 'none') {
        img.style.display = 'block'
      }
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
@import '~assets/style/mixin';

.profile {
  display: flex;
  flex-direction: row;
  justify-content: left;
  // width: fit-content;
  width: 100%;
  height: 100%;
  background-color: inherit;
}
.profile--thumb {
  position: relative;
  flex: 0 0 auto;
  width: 100%;
  height: 100%;
  background-color: inherit;
}

.profile--image {
  width: 100%;
  height: 100%;
  @include image();

  > img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    // background-position: center;
    // background-size: cover;
    // -webkit-mask: url('~assets/image/icon_bg.svg') no-repeat;
    // -webkit-mask-size: 100%;
    // mask: url('~assets/image/icon_bg.svg') no-repeat;
    // mask-size: 100%;
  }
}

.profile--text {
  flex: 1 1 auto;
  width: 100%;
  min-width: 0;
  margin: 0 0.714em 0 1.286em;
}

.profile.profile-short {
  .profile--text {
    max-width: 10em;
  }
}
.profile--maintext {
  color: #fafafa;
  font-weight: 500;
  font-size: 1.071em;
  line-height: 1.429em;
  @include ellipsis;
}
.profile--subtext {
  color: #b7b7b7;
  @include ellipsis;
}

.profile--badge {
  position: absolute;
  left: 66%;
  top: 64%;
  width: 1.333em;
  height: 1.333em;
  border-radius: 50%;
  background-color: inherit;
}

.profile--badge__core {
  width: 0.833em;
  height: 0.833em;
  border-radius: 50%;
  margin: 0.25em;

  &.me {
    background: url('~assets/image/mdpi_icon_me.svg') center;
  }
  &.collabo {
    background-color: $color_collabo;
  }
  &.login {
    background-color: $color_login;
  }
  &.logout {
    background-color: $color_logout;
  }
}
</style>
