<template>
  <div class="profilelist">
    <figure
      class="profilelist-user"
      :style="customStyle"
      v-for="user of showUsers"
      :key="user.id"
    >
      <tooltip
        :content="user.nickName.length === 0 ? user.name : user.nickName"
      >
        <div class="profilelist-user__image" slot="body">
          <profile
            :image="user.profile"
            :thumbStyle="{ width: size, height: size }"
          ></profile>
        </div>
        <!-- <img
          class="profilelist-user__image"
          @error="onImageError"
          :src="user.profile"
          slot="body"
        /> -->
        <span>{{
          user.nickName.length === 0 ? user.name : user.nickName
        }}</span>
      </tooltip>
    </figure>
    <br />
    <popover
      v-if="otherUsers.length > 0"
      placement="right"
      trigger="hover"
      width="10.714em"
      popperClass="otheruser-popover"
      :scrollHide="true"
    >
      <div>
        <figure
          class="profilelist-user otheruser"
          v-for="user of otherUsers"
          :key="user.id"
        >
          <profile
            :image="user.profile"
            :thumbStyle="{ width: '2.571em', height: '2.571em' }"
          ></profile>
          <!-- <img
            class="profilelist-user__image otheruser"
            :src="user.profile"
            @error="onImageError"
          /> -->
          <span class="profilelist-user__name ">{{
            user.nickName.length === 0 ? user.name : user.nickName
          }}</span>
        </figure>
      </div>
      <p slot="reference" class="profilelist-user__expend" :style="customStyle">
        {{ `+${otherUsers.length}` }}
      </p>
    </popover>
  </div>
</template>

<script>
import Tooltip from 'Tooltip'
import Popover from 'Popover'
import Profile from 'Profile'
export default {
  name: 'ProfileList',
  components: {
    Tooltip,
    Popover,
    Profile,
  },
  props: {
    users: {
      type: Array,
      default: () => {
        return []
      },
    },
    customStyle: {
      type: Object,
      default: () => {
        return {}
      },
    },
    max: {
      type: Number,
      default: 6,
    },
    size: {
      type: String,
      default: '2.714em',
    },
  },
  computed: {
    showUsers() {
      if (this.users.length < this.max + 1) {
        return this.users
      } else {
        return this.users.slice(0, this.max - 1)
      }
    },
    otherUsers() {
      if (this.users.length < this.max + 1) {
        return []
      } else {
        return this.users.slice(this.max - 1)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
@import '~assets/style/mixin';

.profilelist {
  display: flex;
  min-height: 2.714em;
}
.profilelist-user {
  width: 2.714em;
  height: 2.714em;
  margin-left: 0.429em;
  // border: solid 1px transparent;
  border-radius: 50%;
  &.otheruser {
    display: flex;
    width: 100%;
    margin-bottom: 0.714em;
    margin-left: 0;
    overflow: unset;
    &:last-child {
      margin-bottom: 0;
    }
  }
  &:first-child {
    margin-left: 0;
  }
}
.profilelist-user__image {
  width: 100%;
  height: 100%;
  @include image();
  &.otheruser {
    width: 2.571em;
    height: 2.571em;
  }
}
.profilelist-user__name {
  display: inline-block;
  flex: 1;
  max-width: 5em;
  margin-left: 0.714em;
  overflow: hidden;
  color: #fff;
  line-height: 2.714em;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.profilelist-user__expend {
  width: 2.714em;
  height: 2.714em;
  margin-left: 0.429em;
  color: #fff;
  line-height: 2.714em;
  text-align: center;
  cursor: default;
  @include profileMask(#3e3e44, 2px, #979fb0);
}
</style>

<style lang="scss">
@import '~assets/style/vars';

.otheruser-popover {
  min-width: 10.714em;
  background-color: $color_bg_sub;
  border: solid 1px #3a3a3d;
  transform: translateX(0.714em);
  .popover--body {
    padding: 1.071em;
  }
}
</style>
