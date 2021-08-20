<template>
  <div
    class="member-group-row"
    :style="{ height: height }"
    :class="{ ...customClass }"
  >
    <div class="group-item">
      <p>{{ index | idxFilter }}</p>
    </div>
    <div class="group-item">
      <p class="title">{{ group.groupName }}</p>
    </div>

    <div class="group-item">
      <p class="label hide-tablet">
        {{ $t('workspace.workspace_member_group_member') }}:
      </p>
      <profile-list
        style="min-height: 0px;"
        :users="group.remoteGroupMemberInfoResponseList"
        size="2.143em"
        :max="isTablet ? 3 : maxParticipants"
        class="member-group-profile"
      ></profile-list>
    </div>

    <div class="member-group__tools">
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
import { maxParticipants } from 'utils/callOptions'

export default {
  name: 'MemberGroup',
  components: {
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
      maxParticipants: maxParticipants,
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

<style lang="scss">
@import '~assets/style/vars';
@import '~assets/style/_mixin';
.member-group-row {
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

  .group-item {
    flex: 1 1 0;

    min-width: 0;
    color: #949495;
    font-weight: 500;
    font-size: 1.0714rem;
    opacity: 0.8;

    & > .label {
      padding-right: 0.5714rem;
      color: rgb(148, 148, 149);
      font-weight: 500;
      font-size: 1.0714rem;
    }

    & > .title {
      overflow: hidden;
      color: rgb(255, 255, 255);
      font-weight: 500;
      font-size: 1.1429rem;
      text-overflow: ellipsis;
    }
  }
  .group-item:first-of-type {
    display: inherit;
    flex-grow: 0.2;

    min-width: 8rem;
  }

  .group-item:nth-of-type(2) {
    flex-grow: 1.4;
  }
  .group-item:nth-last-child(2) {
    display: flex;
    align-items: center;
    padding-right: 0.7143rem;
    overflow: hidden;
  }
}

.member-group-profile.profilelist {
  .profilelist-user__expend {
    width: 2.143em;
    height: 2.143em;
    overflow: hidden;
    line-height: 2.143em;
  }
}

.member-group__tools {
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
