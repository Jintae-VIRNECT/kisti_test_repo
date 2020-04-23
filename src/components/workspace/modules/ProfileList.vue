<template>
  <div class="profilelist">
    <figure
      class="profilelist-user"
      :style="customStyle"
      v-for="user of showUsers"
      :key="user.id"
    >
      <tooltip :content="user.userName">
        <img
          class="profilelist-user__image"
          @error="onImageError"
          :src="user.path"
          slot="body"
        />
        <span>{{ user.userName }}</span>
      </tooltip>
    </figure>
    <br />
    <popover
      v-if="otherUsers.length > 0"
      placement="right"
      trigger="hover"
      :width="150"
      popperClass="otheruser-popover"
    >
      <div>
        <figure
          class="profilelist-user otheruser"
          v-for="user of otherUsers"
          :key="user.id"
        >
          <img
            class="profilelist-user__image otheruser"
            :src="user.path"
            @error="onImageError"
          />
          <span class="profilelist-user__name ">{{ user.userName }}</span>
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
export default {
  name: 'ProfileList',
  components: {
    Tooltip,
    Popover,
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
  min-height: 38px;
}
.profilelist-user {
  width: 38px;
  height: 38px;
  margin-left: 6px;
  border: solid 1px transparent;
  border-radius: 50%;
  &.otheruser {
    display: flex;
    width: 100%;
    margin-bottom: 10px;
    margin-left: 0;
    overflow: unset;
    &:last-child {
      margin-bottom: 0;
    }
  }
}
.profilelist-user__image {
  width: 100%;
  height: 100%;
  @include image();
  &.otheruser {
    width: 36px;
    height: 36px;
  }
}
.profilelist-user__name {
  display: inline-block;
  flex: 1;
  max-width: 70px;
  margin-left: 10px;
  overflow: hidden;
  color: #fff;
  line-height: 38px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.profilelist-user__expend {
  width: 38px;
  height: 38px;
  margin-left: 6px;
  color: #fff;
  font-size: 17px;
  line-height: 38px;
  text-align: center;
  background-color: #3e3e44;
  border: solid 2px #979fb0;
  border-radius: 50%;
  cursor: default;
}
</style>

<style lang="scss">
.otheruser-popover {
  min-width: 150px;
  background-color: #242427;
  border: solid 1px #3a3a3d;
  transform: translateX(10px);
  .popover--body {
    padding: 15px;
  }
}
</style>
