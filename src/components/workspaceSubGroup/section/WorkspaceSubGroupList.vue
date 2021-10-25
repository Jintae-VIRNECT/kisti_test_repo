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
          {{ $t('그룹명') }}
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
          {{ $t('멤버') }}
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
        <!-- rows -->
        <sub-group
          v-for="(subGroup, index) in subGroups.groupInfoResponseList"
          :key="'subGroup' + subGroup.groupId"
          :index="index + 1"
          :subGroup="subGroup"
          :isOverflow="isOverflow"
          @deletegroup="deleteGroup"
          @updategroup="updategroup"
        ></sub-group>
      </template>
      <span v-else class="workspace-sub-group-list__body--nodata">
        <img
          src="~assets/image/image_no_workspace_sub_group_data.svg"
          alt="no data"
        />
        <p>{{ $t('생성된 그룹이 없습니다.') }}</p></span
      >
    </div>
    <pagination-tool
      @current-page="() => []"
      :totalPage="2"
      :currentPage="1"
    ></pagination-tool>

    <modal-workspace-sub-group
      :visible.sync="memberGroupModalFlag"
      :users="
        subGroups.groupInfoResponseList.find(subGroup => {
          return subGroup.groupId === 'group_etc'
        }).remoteGroupMemberResponses
      "
      :groupMembers="[]"
      :groupId="'123'"
      :groupName="'무야호'"
      :total="
        subGroups.groupInfoResponseList.find(subGroup => {
          return subGroup.groupId === 'group_etc'
        }).remoteGroupMemberResponses.length
      "
      @refresh="refreshGroupMember"
    ></modal-workspace-sub-group>
  </section>
</template>

<script>
import ModalWorkspaceSubGroup from '../modal/ModalWorkspaceSubGroup.vue'
import SubGroup from '../partials/WorkspaceSubGroup'
import PaginationTool from 'components/collabo/partials/CollaboPaginationTool'
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
      sort: { column: '', direction: '' },

      subGroups: {
        groupInfoResponseList: [
          {
            workspaceId: '4b2a32a4e091be3d944be6ae2ccf924a',
            groupId: 'group_YOjEA5bzbg',
            groupName:
              '매우긴거매우긴거매우긴거매우긴거매우긴거매우긴거매우긴거매우긴거매우긴거',
            remoteGroupMemberResponses: [
              {
                uuid: '40ced71799059341b7e57b85b10ee350',
                nickName: '왕밤빵14',
                profile: 'default',
                email: 'test14@test.com',
                userType: 'USER',
                name: '홍길동14',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '40247ff4cbe04a1e8ae3203298996f4c',
                nickName: '왕밤빵15',
                profile: 'default',
                email: 'test15@test.com',
                userType: 'USER',
                name: '홍길동15',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
            ],
            memberCount: 2,
          },
          {
            workspaceId: '4b2a32a4e091be3d944be6ae2ccf924a',
            groupId: 'group_etc',
            groupName: '기타',
            remoteGroupMemberResponses: [
              {
                uuid: '4d127135f699616fb88e6bc4fa6d784a',
                nickName: '왕밤빵16',
                profile: 'default',
                email: 'test16@test.com',
                userType: 'USER',
                name: '홍길동16',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4705cf50e6d02c59b0eef9591666e2a3',
                nickName: '왕밤빵17',
                profile: 'default',
                email: 'test17@test.com',
                userType: 'USER',
                name: '홍길동17',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '410df50ca6e32db0b6acba09bcb457ff',
                nickName: '왕밤빵18',
                profile:
                  'https://192.168.6.3:2838/virnect-platform/profile/2021-01-14_eqnixHESRxEXhoQfucan.jpg',
                email: 'test18@test.com',
                userType: 'USER',
                name: '홍길동18',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '473b12854daa6afeb9e505551d1b2743',
                nickName: '왕밤빵19',
                profile: 'default',
                email: 'test19@test.com',
                userType: 'USER',
                name: '홍길동19',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4c9b9bfc0ec7c74781a19331438acdc8',
                nickName: '왕밤빵20',
                profile: 'default',
                email: 'test20@test.com',
                userType: 'USER',
                name: '홍길동20',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4549b97621964b3789034168c8f99714',
                nickName: '왕밤빵21',
                profile: 'default',
                email: 'test21@test.com',
                userType: 'USER',
                name: '홍길동21',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4e6793e995dc57cfb129f8b03fbe7737',
                nickName: '왕밤빵22',
                profile: 'default',
                email: 'test22@test.com',
                userType: 'USER',
                name: '홍길동22',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '487d76f9395db05f9ce40a8a12236b90',
                nickName: '왕밤빵23',
                profile: 'default',
                email: 'test23@test.com',
                userType: 'USER',
                name: '홍길동23',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '41931ae6c664b43ab675abb778a8dc21',
                nickName: '왕밤빵24',
                profile:
                  'https://192.168.6.3:2838/virnect-platform/profile/2021-08-17_hsSWGzDYWVDhUWDKnbbm.png',
                email: 'test24@test.com',
                userType: 'USER',
                name: '홍길동24',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4d3154962003a069be27260a818380a7',
                nickName: '왕밤빵25',
                profile: 'default',
                email: 'test25@test.com',
                userType: 'USER',
                name: '홍길동25',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4ae689a36215c1b894aada41a4d67f59',
                nickName: '왕밤빵26',
                profile: 'default',
                email: 'test26@test.com',
                userType: 'USER',
                name: '홍길동26',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4f941bb7436616a583bc7e6dc7c8e201',
                nickName: '왕밤빵27',
                profile: 'default',
                email: 'test27@test.com',
                userType: 'USER',
                name: '홍길동27',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4b260e69bd6fa9a583c9bbe40f5aceb3',
                nickName: '왕밤빵2',
                profile: 'default',
                email: 'test2@test.com',
                userType: 'USER',
                name: '홍길동2',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '405893f61bec9ac295ca5fa270baca7f',
                nickName: '탈퇴시키지마세요 제발요',
                profile:
                  'https://192.168.6.3:2838/virnect-platform/profile/2020-12-23_oCwKlQsOePnYXgmWptCI.jpg',
                email: 'test3@test.com',
                userType: 'USER',
                name: '홍길동3',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '40467b0c2dd94a83a8c69d70fc54038d',
                nickName: '내꺼임탈퇴ㄴㄴ',
                profile:
                  'https://192.168.6.3:2838/virnect-platform/profile/2020-12-23_wgfMJyqSNSwLpravIWvU.png',
                email: 'test4@test.com',
                userType: 'USER',
                name: '홍길동4',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '489e6ecfb9197afb9d0c55832a0ad832',
                nickName: '왕밤밤빠라라라빵5',
                profile: 'default',
                email: 'test5@test.com',
                userType: 'USER',
                name: '홍길동5',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '434d2aab3bb927fca3d7a9666088ae38',
                nickName: '왕밤빵7',
                profile: 'default',
                email: 'test7@test.com',
                userType: 'USER',
                name: '홍길동7',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4a38d2a83bffc82ebcb1f4b2fc68bc05',
                nickName: '닉네임참길다좀짧게하면안되겠니젭알젭알요',
                profile:
                  'https://192.168.6.3:2838/virnect-platform/profile/2021-03-19_GcqjmuNAgkwsAUwXwpyI.jpg',
                email: 'test8@test.com',
                userType: 'USER',
                name: '홍길동8',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '45004c06dd6dd12cb85d99272d2b0d31',
                nickName: '왕밤빵9',
                profile: 'default',
                email: 'test9@test.com',
                userType: 'USER',
                name: '홍길동9',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4218059539d944fca0a27fc5a57ce05b',
                nickName: '왕밤빵10',
                profile: 'default',
                email: 'test10@test.com',
                userType: 'USER',
                name: '홍길동10',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4d0c92f824a863ac8a6cd9af03c320d5',
                nickName: '왕밤빵12',
                profile: 'default',
                email: 'test12@test.com',
                userType: 'USER',
                name: '홍길동12',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '4a8a6883572afb9b866918f3fbb5c0b5',
                nickName: 'dddd',
                profile:
                  'https://192.168.6.3:2838/virnect-platform/profile/2021-08-10_YjmuJPmmSdLhQKWCyFeg.png',
                email: 'test13@test.com',
                userType: 'USER',
                name: '홍길동13',
                role: 'MEMBER',
                accessType: 'LOGOUT',
              },
              {
                uuid: '45d974ac8cbd3f66b26b0179604d6742',
                nickName: '왕밤빵11',
                profile: 'default',
                email: 'test11@test.com',
                userType: 'USER',
                name: '홍길동11',
                role: 'MASTER',
                accessType: 'LOGOUT',
              },
            ],
            memberCount: 23,
          },
        ],
        memberTotalCount: 25,
      },
    }
  },
  computed: {
    listExists() {
      // return this.historys.length > 0
      // return false
      return true
    },
  },
  methods: {
    addGroup() {
      this.memberGroupModalFlag = true
    },

    async deleteGroup(groupId) {
      this.$eventBus.$emit('popover:close')
      console.log('deleteGroup::', groupId)
      // try {
      //   await deletePrivateMemberGroup({
      //     workspaceId: this.workspace.uuid,
      //     userId: this.account.uuid,
      //     groupId: groupId,
      //   })
      // } catch (err) {
      //   if (err.code) {
      //     this.toastError(this.$t('confirm.network_error'))
      //   }
      // }

      // await this.getMemberGroups()
    },
    async updategroup(groupId) {
      this.$eventBus.$emit('popover:close')
      console.log('updategroup::', groupId)

      // const group = await getMemberGroupItem({
      //   workspaceId: this.workspace.uuid,
      //   groupId: groupId,
      //   userId: this.account.uuid,
      // })

      // //자기자신은 제외
      // this.groupMemberList = group.favoriteGroupMemberResponses.filter(
      //   member => {
      //     return member.uuid !== this.account.uuid
      //   },
      // )

      // //그룹 수정 모달 출력
      // this.selectedGroupId = groupId
      // this.selectedGroupName = group.groupName
      // this.memberGroupModalFlag = true
    },

    async refreshGroupMember(groupId) {
      console.log('refreshGroupMember::', groupId)
      // if (groupId) {
      //   //자기자신은 제외
      //   const group = await getMemberGroupItem({
      //     workspaceId: this.workspace.uuid,
      //     groupId: groupId,
      //     userId: this.account.uuid,
      //   })
      //   this.groupMemberList = group.favoriteGroupMemberResponses.filter(
      //     member => {
      //       return member.uuid !== this.account.uuid
      //     },
      //   )
      //   //그룹 수정 모달 출력
      //   this.selectedGroupId = groupId
      //   this.selectedGroupName = group.groupName
      // } else {
      //   this.getList()
      // }
    },

    setSort() {},
  },
  mounted() {
    this.$eventBus.$on('open::addSubGroupList', this.addGroup)
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
