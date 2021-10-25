<template>
  <figure class="profile" :class="status" v-on="{ ...$listeners }">
    <div class="profile--thumb" :style="thumbStyle">
      <div class="profile--image" :class="{ group: group, image: useImage }">
        <img
          v-if="useImage"
          :src="imgSrc"
          :alt="mainText"
          @error="onImageError"
        />
      </div>
      <!-- <span v-if="status" class="profile--badge" :class="status">{{
        status
      }}</span> -->
    </div>
    <figcaption class="profile--text" v-if="mainText && mainText.length > 0">
      <p class="profile--maintext">{{ mainText }}</p>
      <p class="profile--subtext" v-if="subText">{{ subText }}</p>
    </figcaption>
    <role v-if="showRole" :role="role">{{ 'Master' }}</role>
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
      validator: value => ['', 'online', 'busy', 'offline'].indexOf(value) >= 0,
    },
    role: {
      type: String,
      default: null,
    },
    group: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    imgSrc() {
      return proxyUrl(this.image)
    },
    showRole() {
      if (this.role === WORKSPACE_ROLE.MEMBER || this.role === ROLE.UNKNOWN) {
        return false
      } else {
        return true
      }
    },
    useImage() {
      if (this.image && this.image.length > 0 && this.image !== 'default') {
        return this.image
      } else {
        return false
      }
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
}
.profile--thumb {
  position: relative;
  flex: 0 0 auto;
  width: 100%;
  height: 100%;
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
  color: #262626;
  font-weight: 500;
  font-size: 1.071em;
  line-height: 1.429em;
  @include ellipsis;
}
.profile--subtext {
  color: #757f91;
  @include ellipsis;
}

.profile--badge {
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
</style>
