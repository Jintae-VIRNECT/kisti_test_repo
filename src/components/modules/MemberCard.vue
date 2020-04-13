<template>
  <div class="card" :style="{ width: cardWidth, height: height + 'px' }">
    <div class="profile--thumb">
      <div
        v-if="color.length > 0"
        class="profile--image"
        :style="{ 'background-color': color }"
      ></div>
      <p v-else class="profile--image">
        <img :src="imageUrl" />
      </p>
      <span v-if="status" class="profile--badge" :class="status">{{
        status
      }}</span>
    </div>
    <div>{{ name }}</div>
    <div>{{ email }}</div>
    <role v-if="role" :role="role">{{ role }}</role>
    <!-- <popover v-if="menu" trigger="click" placement="bottom-start">
      <button slot="reference" class="card__button"></button>
      <slot name="menuPopover"></slot>

      <div>
        버튼
      </div>
    </popover>
    <slot></slot> -->
  </div>
</template>

<script>
import Popover from 'Popover'
import Role from 'Role'
export default {
  name: 'Card',
  components: {
    Popover,
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
    width: {
      type: [Number, String],
      default: 204,
    },
    height: {
      type: Number,
      default: 244,
    },
    menu: {
      type: Boolean,
      default: false,
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
  position: relative;
  padding: 30px;
  background-color: #313135;
  border: solid 1px #3e3e42;
  border-radius: 2px;
  > .popover--wrapper {
    position: absolute;
    top: 16px;
    right: 16px;
  }
}
.card__button {
  width: 28px;
  height: 28px;
  background: url(~assets/image/ic-more-horiz-light.svg) 50% no-repeat;
}

.profile {
  display: flex;
  flex-direction: row;
  justify-content: left;
  width: fit-content;
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
  margin: 0 10px 0 18px;
}
.profile--maintext {
  color: #fafafa;
  font-weight: 500;
  font-size: 15px;
  line-height: 20px;
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
