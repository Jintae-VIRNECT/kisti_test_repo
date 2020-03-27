<template>
  <figure class="profile" :class="status" v-on="{ ...$listeners }">
    <div class="profile--thumb">
      <p class="profile--image">
        <img :src="image" :alt="imageAlt" @error="onError" />
      </p>
      <span v-if="status" class="profile--badge" :class="status">{{
        status
      }}</span>
    </div>
    <figcaption class="profile--text">
      <p class="profile--maintext">{{ mainText }}</p>
      <p class="profile--subtext" v-if="subText">{{ subText }}</p>
    </figcaption>
  </figure>
</template>

<script>
export default {
  name: 'Profile',
  props: {
    image: String,
    imageAlt: {
      type: String,
      default: function() {
        return 'thumbnail image'
      },
    },
    onError: {
      type: Function,
      default: function() {},
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
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';

.profile {
  &--thumb {
    position: relative;
  }

  &--image {
    width: 100%;
    height: 100%;
    overflow: hidden;
    line-height: 0;
    background-color: #fff;
    border-radius: 50%;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  &--badge {
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
}
</style>
