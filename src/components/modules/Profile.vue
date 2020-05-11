<template>
  <figure class="profile" :class="status" v-on="{ ...$listeners }">
    <div class="profile--thumb" :style="thumbStyle">
      <div class="profile--image">
        <img
          v-if="image && image.length > 0"
          :src="image"
          :alt="mainText"
          @error="onImageError"
        />
      </div>
      <span v-if="status" class="profile--badge" :class="status">{{
        status
      }}</span>
    </div>
    <figcaption class="profile--text" style="color: #fff;">
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
      required: true,
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
  width: fit-content;
}
.profile--thumb {
  position: relative;
  width: 3em;
  height: 3em;
}

.profile--image {
  width: 100%;
  height: 100%;
  overflow: hidden;
  line-height: 0;
  background: url('~assets/image/icon_bg.svg') no-repeat;
  background-size: 100%;
  &:before {
    position: absolute;
    top: 0;
    left: 0;
    width: 80%;
    height: 80%;
    margin: 10%;
    background: url('~assets/image/icon_user_profile.svg') no-repeat;
    background-size: 100%;
  }

  > img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    background-position: center;
    background-size: cover;
    // -webkit-mask: url('~assets/image/icon_bg.svg') no-repeat;
    // -webkit-mask-size: 100%;
    mask: url('~assets/image/icon_bg.svg') no-repeat;
    mask-size: 100%;
    @include image();
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
