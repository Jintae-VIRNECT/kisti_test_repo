<template>
  <div
    class="workspace-sub-group"
    :style="{ height: height }"
    :class="{ ...customClass }"
  >
    <div class="sub-group-item">
      <p>{{ index | idxFilter }}</p>
    </div>
    <div class="sub-group-item">
      <p class="title">{{ subGroup.groupName }}</p>
    </div>

    <div class="sub-group-item member-list">
      <p class="label">{{ $t('그룹 멤버') }} :</p>
      <profile-list
        style="min-height: 0px"
        :users="subGroup.remoteGroupMemberResponses"
        size="2.143em"
        :max="isOverflow ? 3 : maxParticipants"
        class="sub-group-profile"
      ></profile-list>
    </div>

    <div class="sub-group__tools">
      <button @click="updateGroup" class="update" :class="{ hover: isHover }">
        {{ $t('수정') }}
      </button>
      <button @click="deleteGroup" class="delete" :class="{ hover: isHover }">
        {{ $t('삭제') }}
      </button>
    </div>
  </div>
</template>

<script>
import ProfileList from 'ProfileList'
import { maxParticipants } from 'utils/callOptions'

export default {
  name: 'WorkspaceSubGroup',
  components: {
    ProfileList,
  },
  props: {
    subGroup: Object,
    index: {
      type: Number,
    },
    height: {
      type: [Number, String],
      default: 86,
    },
    customClass: {
      type: Object,
      default: () => {
        return {}
      },
    },
    isOverflow: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      showRoomInfo: false,
      maxParticipants: maxParticipants,

      isHover: false,
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
      console.log('update group')
      this.$emit('updategroup', this.subGroup.groupId)
    },
    deleteGroup() {
      console.log('delete group')
      this.$emit('deletegroup', this.subGroup.groupId)
    },
  },

  /* Lifecycles */
}
</script>

<style lang="scss">
@import '~assets/style/vars';

.workspace-sub-group {
  display: flex;
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  height: 5.1429rem;

  margin-bottom: 0.7143rem;
  padding: 1.571em 2.143em;
  background-color: #ffffff;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  box-shadow: 0px 6px 12px 0px rgba(0, 0, 0, 0.05);
  transition: 0.3s;

  &:hover {
    background: rgb(245, 249, 255);
    border: 1px solid rgb(147, 195, 255);
  }

  .sub-group-item {
    flex: 1 1 0;

    min-width: 0;
    color: #949495;
    font-weight: 500;
    font-size: 1.0714rem;
    opacity: 0.8;

    & > .label {
      padding-right: 0.5714rem;
      color: #949495;
      font-weight: 500;
      font-size: 1.0714rem;
    }

    & > .title {
      overflow: hidden;
      color: #262626;
      font-weight: 500;
      font-size: 1.1429rem;
      text-overflow: ellipsis;
    }
  }
  //number
  .sub-group-item:first-of-type {
    display: inherit;
    flex-grow: 0.2;
    min-width: 8rem;
  }
  //title
  .sub-group-item:nth-of-type(2) {
    flex-grow: 1;
  }
  .sub-group-item.member-list {
    display: flex;
    align-items: center;
    padding-right: 0.7143rem;
  }
}

.sub-group-profile.profilelist {
  .profilelist-user__expend {
    width: 2.143em;
    height: 2.143em;
    overflow: hidden;
    line-height: 2.143em;

    &:before {
      background-color: #adb3bd;
    }
  }
}

.sub-group__tools {
  display: flex;
  align-items: center;
  justify-content: flex-end;

  & > button {
    width: 7.2857rem;
    height: 2.7143rem;
    margin-left: 0.7143rem;
    background: #595960;
    border-radius: 2px;
    transition: 0.3s;

    &:focus,
    &:active {
      background-color: $color_darkgray_400;
    }

    &.delete {
      background-color: #757f91;
      &:hover {
        background-color: #b8bfcd;
      }
    }

    &.update {
      background-color: #9fa6b3;
      &:hover {
        background-color: #b8bfcd;
      }
    }
  }
}
</style>
