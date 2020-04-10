<template>
  <div class="profilelist">
    <figure class="profilelist-user" v-for="user of showUsers" :key="user.id">
      <img class="profilelist-user__image" :src="user.image" />
    </figure>
    <br />
    <popover placement="right" trigger="hover" v-if="otherUsers.length > 0">
      <div>
        <figure
          class="profilelist-user"
          v-for="user of otherUsers"
          :key="user.id"
        >
          <img class="profilelist-user__image" :src="user.image" />
        </figure>
      </div>
      <p slot="reference" class="profilelist-user__expend">
        {{ `+${otherUsers.length}` }}
      </p>
    </popover>
  </div>
</template>

<script>
import Popover from 'Popover'
export default {
  name: 'ProfileList',
  components: {
    Popover,
  },
  props: {
    users: {
      type: Array,
      default: () => {
        return []
      },
    },
  },
  computed: {
    showUsers() {
      if (this.users.length < 7) {
        return this.users
      } else {
        return this.users.slice(0, 5)
      }
    },
    otherUsers() {
      if (this.users.length < 7) {
        return []
      } else {
        return this.users.slice(5)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';

.profilelist {
  display: flex;
  min-height: 38px;
}
.profilelist-user {
  width: 38px;
  height: 38px;
  margin-left: 6px;
  overflow: hidden;
  border: solid 1px transparent;
  border-radius: 50%;
}
.profilelist-user__image {
  width: 100%;
  height: 100%;
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
