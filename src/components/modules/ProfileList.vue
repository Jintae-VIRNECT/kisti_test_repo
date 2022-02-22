<template>
  <div class="profilelist">
    <figure
      class="profilelist-user"
      :style="customStyle"
      v-for="(user, idx) of showUsers"
      :key="user.uuid"
    >
      <tooltip :content="user.nickName">
        <div class="profilelist-user__image" slot="body">
          <profile
            :image="user.profile"
            :thumbStyle="{ width: size, height: size }"
            :remove="isRemoveActive(user)"
            @remove="onRemove(user)"
            :status="accessType(user.accessType)"
          ></profile>
          <button
            v-if="isRemoveActive(user) === false"
            class="kickout-btn"
            @click="kickout(user, idx)"
          >
            {{ $t('button.kickout') }}
          </button>
        </div>
        <!-- <img
          class="profilelist-user__image"
          @error="onImageError"
          :src="user.profile"
          slot="body"
        /> -->
        <span>{{ user.nickName }}</span>
      </tooltip>
      <p v-if="showNickname" class="profilelist-user__nickname">
        {{ user.nickName }}
      </p>
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
          :key="user.uuid"
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
          <span class="profilelist-user__name ">{{ user.nickName }}</span>
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
    remove: {
      type: Boolean,
      default: false,
    },
    showNickname: {
      type: Boolean,
      default: false,
    },
    showStatus: {
      type: Boolean,
      default: false,
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
  methods: {
    isRemoveActive(user) {
      return this.remove && !user.currentInvited
    },
    onRemove(user) {
      this.$emit('remove', user)
    },
    accessType(accessType) {
      if (this.showStatus) {
        if (accessType) return accessType.toLowerCase()
        return ''
      } else return null
    },
    kickout(participant, idx) {
      this.$emit('kickout', { participant, idx })
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
  width: fit-content;
  height: fit-content;
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

  > .tooltip {
    width: 100%;
    height: 100%;
  }
  > .profile {
    width: auto;
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
  .kickout-btn {
    display: none;
  }
}
.profilelist-user__nickname {
  display: none;
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
  @include profileMask(#3e3e44, 1px, #979fb0);
}

@include responsive-mobile {
  .profilelist-user__image {
    width: 4.3rem;
    height: 4.3rem;
    background-color: $new_color_bg_sub;
    .kickout-btn {
      position: absolute;
      top: 0;
      left: 0;
      display: block;
      width: 1.429em;
      height: 1.429em;
      background-color: #ff3939;
      border-radius: 50%;
      @include ir;
      &:after {
        position: absolute;
        top: 50%;
        left: 50%;
        width: 0.714em;
        height: 1px;
        background-color: #fff;
        transform: translate(-50%, -50%);
        content: '';
      }
    }
  }
  .profilelist-user__nickname {
    display: block;
    max-width: 6rem;
    margin-top: 0.3rem;
    overflow: hidden;
    white-space: nowrap;
    text-align: center;
    text-overflow: ellipsis;
    @include fontLevel(50);
  }
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
