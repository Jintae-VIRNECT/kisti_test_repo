<template>
  <figure class="profile" :class="status" v-on="{ ...$listeners }">
    <div class="profile--thumb">
      <div
        v-if="color.length > 0"
        class="profile--image"
        :style="{ 'background-color': color }"
      >
        <p>{{ mainText.slice(0, 1) }}</p>
      </div>
      <p v-else class="profile--image">
        <img :src="image" :alt="imageAlt" @error="errorEvent" />
      </p>
      <span v-if="status" class="profile--badge" :class="status">{{
        status
      }}</span>
    </div>
    <figcaption class="profile--text" style="color: #fff;">
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
    color: {
      type: String,
      default: '',
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
  display: flex;
  flex-direction: row;
  justify-content: left;
}
.profile--thumb {
  position: relative;
  width: 42px;
  height: 42px;
}

.profile--image {
  width: 100%;
  height: 100%;
  overflow: hidden;
  line-height: 0;
  background-color: #fff;
  border-radius: 50%;

  > img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  > p {
    width: fit-content;
    margin: auto;
    color: #fff;
    font-size: 16px;
    line-height: 42px;
  }
}

.profile--text {
  margin-left: 18px;
}
.profile--maintext {
  color: #fafafa;
  font-weight: 500;
  font-size: 15px;
  line-height: 22px;
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
