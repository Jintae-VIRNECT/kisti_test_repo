<template>
  <figure class="profile" :class="status" v-on="{ ...$listeners }">
    <div class="profile--thumb" :style="thumbStyle">
      <div class="profile--image" :class="{ group: group, image: useImage }">
        <img
          v-if="useImage"
          :src="image"
          :alt="mainText"
          @error="onImageError"
        />
      </div>
      <!-- <span v-if="status" class="profile--badge" :class="status">{{
        status
      }}</span> -->
    </div>
    <figcaption
      class="profile--text"
      style="color: #fff;"
      v-if="mainText && mainText.length > 0"
    >
      <p class="profile--maintext">{{ mainText }}</p>
      <p class="profile--subtext" v-if="subText">{{ subText }}</p>
    </figcaption>
    <role v-if="role && role.toLowerCase() === 'master'" :role="role">{{
      'Master'
    }}</role>
  </figure>
</template>

<script>
import Role from 'Role'
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
    useImage() {
      if (this.image && this.image.length > 0 && this.image !== 'default') {
        return this.image
      } else {
        return false
      }
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
@import '~assets/style/mixin';

.profile {
  display: inline-flex;
  flex-direction: row;
  justify-content: left;
  width: fit-content;
  width: 100%;
  height: 100%;
}
.profile--thumb {
  position: relative;
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
  margin: 0 0.714em 0 1.286em;
}
.profile--maintext {
  color: #fafafa;
  font-weight: 500;
  font-size: 1.071em;
  line-height: 1.429em;
}
.profile--subtext {
  color: #b7b7b7;
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
