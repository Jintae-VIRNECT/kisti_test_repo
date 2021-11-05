<template>
  <section class="workspace-sub-group-list">
    <div class="workspace-sub-group-list__header">
      <div class="workspace-sub-group-list__header--text index">
        <span
          @click="setSort('INDEX')"
          :class="{ active: sort.column === 'INDEX' }"
        >
          No
        </span>
      </div>
      <div class="workspace-sub-group-list__header--text group-name">
        <span
          @click="setSort('INDEX')"
          :class="{
            active: sort.column === 'GROUPNAME',
          }"
        >
          {{ $t('subgroup.group_name') }}
        </span>
      </div>
      <div
        class="workspace-sub-group-list__header--text member"
        :class="{ isOverflow }"
      >
        <span
          @click="setSort('INDEX')"
          :class="{ active: sort.column === 'MEMBER' }"
        >
          {{ $t('subgroup.member') }}
        </span>
      </div>
    </div>

    <div
      class="workspace-sub-group-list__body"
      :class="{ nodata: !listExists }"
    >
      <span
        v-if="loading"
        class="workspace-sub-group-list__body--loading"
      ></span>
      <template v-else-if="!loading && listExists">
        <sub-group
          v-for="subGroup in subGroupsWithoutEtc"
          :key="'subGroup' + subGroup.groupId"
          :index="subGroup.index"
          :subGroup="subGroup"
          :isOverflow="isOverflow"
          @deletegroup="deleteGroup"
          @updategroup="updateGroup"
        ></sub-group>
      </template>
      <span v-else class="workspace-sub-group-list__body--nodata">
        <img
          src="~assets/image/image_no_workspace_sub_group_data.svg"
          alt="no data"
        />
        <p>{{ $t('subgroup.no_group') }}</p></span
      >
    </div>
    <pagination-tool
      @current-page="updateCurrentPage"
      :totalPage="totalPage"
      :currentPage="currentPage"
    ></pagination-tool>

    <modal-workspace-sub-group
      :visible.sync="memberGroupModalFlag"
      :users="users"
      :groupMembers="groupMembers"
      :groupId="groupId"
      :groupName="groupName"
      :total="users.length"
      :modalLoading="modalLoading"
      @refresh="refreshGroupMember"
    ></modal-workspace-sub-group>
  </section>
</template>

<script>
import ModalWorkspaceSubGroup from '../modal/ModalWorkspaceSubGroup.vue'
import SubGroup from '../partials/WorkspaceSubGroup'
import PaginationTool from 'components/collabo/partials/CollaboPaginationTool'

import {
  getSubGroups,
  getSubGroupItem,
  deleteSubGroup,
} from 'api/http/subGroup'

const GROUP_COUNT_PER_PAGE = 7

export default {
  name: 'WorkspaceSubGroupList',
  components: {
    SubGroup,
    PaginationTool,
    ModalWorkspaceSubGroup,
  },
  props: {
    isOverflow: {
      type: Boolean,
      default: false,
    },
  },

  data() {
    return {
      memberGroupModalFlag: false,
      loading: false,
      modalLoading: false,
      sort: { column: '', direction: '' },

      subGroups: [],
      subGroupsWithoutEtc: [],
      groupMembers: [],
      groupId: '',
      groupName: '',

      //선택가능한 유저
      users: [],

      currentPage: 1,
    }
  },
  computed: {
    listExists() {
      return this.subGroupsWithoutEtc.length > 0
    },
    totalPage() {
      const divided = (this.subGroups.length - 1) / GROUP_COUNT_PER_PAGE
      if (Number.isInteger(divided)) {
        return divided
      } else {
        return Math.trunc(divided) + 1
      }
    },
  },
  watch: {
    subGroups() {
      if (this.currentPage > this.totalPage) {
        this.currentPage = this.totalPage
      }

      if (this.subGroups.length === 0) {
        this.currentPage = 0
      }

      this.updateEtcGroup()
    },

    memberGroupModalFlag(cur, prev) {
      if (prev === true && cur === false) {
        this.getSubGroups()

        //reset
        this.groupMembers = []
        this.groupId = ''
        this.groupName = ''
        this.users = []
      }
    },

    currentPage() {
      this.updateEtcGroup()
    },
  },
  methods: {
    async getSubGroups() {
      this.loading = true
      try {
        const groupsData = await getSubGroups({
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
        })

        this.subGroups = groupsData.groupInfoResponseList

        const etcMemberCount = this.subGroups.find(subGroup => {
          return subGroup.groupId === 'group_etc'
        }).remoteGroupMemberResponses.length

        this.$eventBus.$emit('update::etcMemberCount', etcMemberCount)
        this.$eventBus.$emit(
          'update::updateSubGroupCount',
          this.subGroups.length - 1,
        )
      } catch (err) {
        console.error(err)
      }
      this.loading = false
    },
    addGroup() {
      this.memberGroupModalFlag = true
      this.users = this.subGroups.find(subGroup => {
        return subGroup.groupId === 'group_etc'
      }).remoteGroupMemberResponses

      this.groupMembers = this.users
    },
    async deleteGroup(groupId) {
      try {
        await deleteSubGroup({
          groupId: groupId,
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })
      } catch (err) {
        console.error(err)
      }

      await this.getSubGroups()
    },
    async updateGroup(groupId) {
      await this.getSubGroupItem(groupId)
      this.memberGroupModalFlag = true
    },

    async refreshGroupMember(groupId) {
      this.modalLoading = true
      if (groupId) {
        await this.getSubGroupItem(groupId)
      } else {
        await this.getSubGroups()
        this.users = this.subGroups.find(subGroup => {
          return subGroup.groupId === 'group_etc'
        }).remoteGroupMemberResponses
      }

      this.modalLoading = false
    },
    updateEtcGroup() {
      const etcIndex = this.subGroups.findIndex(subGroup => {
        return subGroup.groupId === 'group_etc'
      })

      if (etcIndex > -1) {
        this.subGroupsWithoutEtc = [...this.subGroups]
        this.subGroupsWithoutEtc.splice(etcIndex, 1)
      } else {
        this.subGroupsWithoutEtc = []
      }

      let from = null
      let to = null

      if (this.currentPage > 0) {
        from = (this.currentPage - 1) * GROUP_COUNT_PER_PAGE
        to = this.currentPage * GROUP_COUNT_PER_PAGE
      } else {
        from = 0
        to = GROUP_COUNT_PER_PAGE
      }

      this.subGroupsWithoutEtc = this.subGroupsWithoutEtc.slice(from, to)
    },
    updateCurrentPage(page) {
      this.currentPage = page
    },
    async getSubGroupItem(groupId) {
      const group = await getSubGroupItem({
        groupId: groupId,
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })

      this.groupId = group.groupId
      this.groupName = group.groupName
      this.groupMembers = group.remoteGroupMemberResponses

      const etcMembers = this.subGroups.find(subGroup => {
        return subGroup.groupId === 'group_etc'
      }).remoteGroupMemberResponses

      this.users = group.remoteGroupMemberResponses.concat(etcMembers)
    },
    setSort() {},
  },
  async mounted() {
    this.$eventBus.$on('open::addSubGroupList', this.addGroup)
    await this.getSubGroups()
  },
  beforeDestroy() {
    this.$eventBus.$off('open::addSubGroupList', this.addGroup)
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.workspace-sub-group-list {
  // min-height: 44rem;
}

.workspace-sub-group-list__body {
  min-height: 44rem;
  &.nodata {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 30.7143rem;
    background: #eaedf3;
    border: 1px solid $color_border;
    border-radius: 10px;
    box-shadow: 0px 6px 12px 0px rgba(0, 0, 0, 0.05);

    p {
      color: #686868;
      font-weight: normal;
      font-size: 1.4286rem;
      text-align: center;
    }
  }
}

.workspace-sub-group-list__header {
  position: relative;
  display: flex;

  align-items: center;
  justify-content: flex-start;
  height: 3.1429rem;
}
.workspace-sub-group-list__header--text {
  color: rgb(74, 83, 97);
  font-weight: normal;
  font-size: 0.8571rem;

  &.index {
    flex: 0.7 1 0;
    margin-left: 1.7857rem;
  }

  &.group-name {
    position: absolute;
    left: 10rem;
  }
  &.member {
    flex: 0.88 1 0;

    &.isOverflow {
      flex: 0.95 1 0;
    }
  }

  & > span {
    &:hover {
      cursor: pointer;
    }

    &.active {
      font-weight: bold;
    }

    &::after {
      display: inline-block;
      width: 1rem;
      height: 0.7143rem;
      background: url(~assets/image/ic_list_up.svg) center/100% no-repeat;
      content: '';
    }
  }
}

.workspace-sub-group-list__body--loading {
  color: transparent;
  &:after {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5.7143rem;
    height: 5.7143rem;
    background: center center/40px 40px no-repeat url(~assets/image/loading.gif);
    transform: translate(-50%, -50%);
    content: '';
  }
}

.workspace-sub-group-list__body--nodata {
  display: flex;
  flex-direction: column;
  color: $color_text_main;
  font-weight: 500;
  font-size: 1.2857rem;
}
</style>
