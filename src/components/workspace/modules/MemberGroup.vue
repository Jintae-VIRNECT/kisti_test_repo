<template>
  <div class="widecard" :style="{ height: height }" :class="{ ...customClass }">
    <div class="card-item">
      <p>{{ index | idxFilter }}</p>
    </div>
    <div class="card-item">
      <p class="title">{{ group.groupName }}</p>
    </div>

    <div class="card-item">
      <p class="label">{{ $t('workspace.workspace_member_group_member') }}:</p>
      <profile-list
        style="min-height: 0px;"
        class="member-group"
        :users="group.remoteGroupMemberInfoResponseList"
        size="2.143em"
      ></profile-list>
    </div>

    <div class="widecard-tools">
      <button @click="updateGroup">
        {{ $t('button.modify') }}
      </button>
      <button @click="deleteGroup">
        {{ $t('button.delete') }}
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
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  height: 5.1429rem;

  margin-bottom: 0.7143rem;
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
    font-size: 1.0714rem;
    opacity: 0.8;

    & > .label {
      padding-right: 8px;
      color: rgb(148, 148, 149);
      font-weight: 500;
      font-size: 1.0714rem;
    }

    & > .title {
      color: rgb(255, 255, 255);
      font-weight: 500;
      font-size: 1.1429rem;
    }
  }
  .card-item:first-of-type {
    display: inherit;
    flex-grow: 0.2;

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
    width: 7.2857rem;
    height: 2.7143rem;
    margin-left: 0.7143rem;
    background: #595960;
    border-radius: 2px;
    box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.16);

    &:hover {
      background: #8f8f9b;
      transition: background-color 0.2s;
    }
    &:focus,
    &:active {
      background-color: $color_darkgray_400;
    }
  }
}
</style>
