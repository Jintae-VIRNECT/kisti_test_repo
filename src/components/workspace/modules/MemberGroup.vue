<template>
  <div class="widecard" :style="{ height: height }" :class="{ ...customClass }">
    <div class="card-item">
      <p>{{ index | idxFilter }}</p>
    </div>
    <div class="card-item">
      {{ group.groupName }}
    </div>

    <div class="card-item">
      <p class="label">그룹 멤버:</p>
      <profile-list
        style="min-height: 0px;"
        class="member-group"
        :users="group.remoteGroupMemberInfoResponseList"
        size="2.143em"
      ></profile-list>
    </div>

    <div class="widecard-tools ">
      <button @click="updateGroup">
        수정
      </button>
      <button @click="deleteGroup">
        삭제
      </button>
    </div>
  </div>
</template>

<script>
import ProfileList from 'ProfileList'

export default {
  name: 'MemberGroup',
  components: {
    // Popover,
    ProfileList,
  },
  props: {
    group: Object,
    index: {
      type: Number,
    },
    height: {
      type: [Number, String],
      default: 86,
    },
    menu: {
      type: Boolean,
      default: false,
    },
    customClass: {
      type: Object,
      default: () => {
        return {}
      },
    },
    room: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {
      showRoomInfo: false,
    }
  },

  filters: {
    idxFilter(idx) {
      if (idx > 9) {
        return idx
      } else {
        return '0' + idx
      }
    },
  },
  computed: {},
  methods: {
    updateGroup() {
      this.$emit('updategroup', this.group.groupId)
    },
    deleteGroup() {
      this.$emit('deletegroup', this.group.groupId)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
@import '~assets/style/_mixin';
.widecard {
  display: flex;
  // flex-basis: 0px;
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  margin-bottom: 0.571em;
  padding: 1.571em 2.143em;
  background-color: $color_darkgray_600;
  border-radius: 2px;
  transition: background-color 0.3s;

  &:hover {
    background-color: $color_darkgray_500;
  }

  .card-item {
    flex: 1 1 0;

    min-width: 0;
    color: #949495;
    font-weight: 500;
    font-size: 15px;
    opacity: 0.8;

    & > .label {
      padding-right: 8px;
      color: rgb(148, 148, 149);
      font-weight: 500;
      font-size: 15px;
    }
  }
  .card-item:first-of-type {
    display: inherit;
    flex-grow: 0.5;

    min-width: 112px;
  }

  .card-item:nth-of-type(2) {
    flex-grow: 1.4;
  }
  .card-item:nth-last-child(2) {
    display: flex;
    align-items: center;
    padding-right: 0.7143rem;
  }
  .card-item:last-of-type {
  }
}

.widecard-tools {
  display: flex;
  align-items: center;
  justify-content: flex-end;

  & > button {
    width: 102px;
    height: 38px;
    margin-left: 10px;
    background: rgb(89, 89, 96);
    border-radius: 2px;
    box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.16);
  }
}
</style>
