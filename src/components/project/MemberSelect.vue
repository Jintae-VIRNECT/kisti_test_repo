<template>
  <div class="project-member-select">
    <el-divider />
    <el-row>
      <el-button type="text" class="undo" @click="undo">
        <img src="~assets/images/icon/ic-undo.svg" />
      </el-button>
    </el-row>
    <!-- 프로젝트 공유 / 편집 정보 -->
    <dl class="select" ref="form">
      <dt>{{ $t(selectLabel) }}</dt>
      <dd class="virnect-workstation-form">
        <el-select v-model="memberPermissions">
          <el-option
            v-for="type in selectOptions"
            :key="type.value"
            :value="type.value"
            :label="$t(type.label)"
          />
        </el-select>
        <el-button
          type="is-disabled"
          @click="$emit('update:updateProjectAuth', propsKey)"
        >
          {{ $t('common.apply') }}
        </el-button>
      </dd>
      <!-- 지정 멤버 드롭다운 메뉴 -->
      <dt>{{ $t('projects.info.project.selectMember') }}</dt>
      <dd class="virnect-workstation-form">
        <span class="input-placeholder" :style="cssVars">
          <img src="~assets/images/icon/ic-search.svg" />
          <span>{{ $t('projects.info.project.selectMember') }}</span>
        </span>
        <el-select
          ref="memberSelect"
          class="select-member"
          popper-class="select-member__list"
          v-model="selectMemberArray"
          name="input"
          multiple
          collapse-tags
          placeholder=""
          @change="selectChange()"
          :disabled="disabledSelect"
        >
          <el-option
            v-for="item in members"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
            <!-- 유저 프로필 유저 닉네임 -->
            <VirnectThumbnail :size="22" :image="item.img" />
            <span>{{ item.label }}</span>
          </el-option>
        </el-select>

        <el-button
          type="is-disabled"
          :disabled="disabledSelect"
          @click="$emit('update:updateProjectMember', propsKey)"
        >
          {{ $t('common.apply') }}
        </el-button>

        <!-- 지정 유저 라벨 -->
        <div v-if="selectMemberArray.length > 0" class="label-user">
          <el-tooltip
            effect="dark"
            :content="getLabelName"
            placement="right-start"
          >
            <div>
              <VirnectThumbnail
                :image="labelImg"
                :defaultImage="$defaultUserProfile"
              />
              <span>{{ getLabelName }}</span>
              <img
                @click="deleteFirstUser"
                class="delete"
                src="~assets/images/icon/ic-close.svg"
              />
            </div>
          </el-tooltip>

          <div v-if="selectMemberArray.length > 1">
            + {{ selectMemberArray.length - 1 }}
          </div>
        </div>
      </dd>
    </dl>
  </div>
</template>

<script>
export default {
  props: {
    selectLabel: String,
    selectOptions: [Array],
    memberPermission: String,
    selectMembers: [Array],
    members: [Array],
  },
  data() {
    return {
      // 현재 컴포넌트의 key 값.
      propsKey: this.$vnode.key,
      // 지정멤버 라벨 이미지
      labelImg: '',
      // 지정멤버 드롭다운 활성화 유무
      disabledSelect: true,
      // 공유 / 편집 멤버 타입. ex) 멤버, 지정멤버, 매니저 ...
      memberPermissions: this.memberPermission,
      // 지정 멤버로 선택한 유저 리스트
      selectMemberArray: this.selectMembers,
    }
  },
  computed: {
    cssVars() {
      return {
        '--cursor': this.disabledSelect ? 'not-allowed' : 'pointer',
        '--pointer': this.disabledSelect ? 'all' : 'none',
      }
    },
    disableState() {
      return this.memberPermissions !== 'SPECIFIC_MEMBER'
    },
    getLabelName() {
      const member = this.members.find(
        member => member.value === this.selectMemberArray[0],
      )
      return member ? member.label : this.$t('members.deletedUser')
    },
  },
  methods: {
    // 지정 멤버 드롭다운 메뉴에서 유저 선택시, 실행하는 메소드
    selectChange() {
      this.setLabelImage()
    },
    // 선택한 지정 멤버 리스트에서, 첫 번째 인덱스 멤버를 삭제한다. 라벨의 X 버튼 클릭시 실행.
    deleteFirstUser() {
      if (this.selectMemberArray.length) {
        this.selectMemberArray = this.selectMemberArray.slice(1)
        this.setLabelImage()
      }
    },
    setLabelImage() {
      // 선택한 유저 수가 0명 이상일 때,
      if (this.selectMemberArray.length) {
        // 선택된 유저 리스트에서 첫 유저의 프로필 사진을 대표 라벨 이미지로 보여주기
        const member = this.members.find(
          member => member.value === this.selectMemberArray[0],
        )
        this.labelImg = member ? member.img : this.$defaultUserProfile
      }
    },
    undo() {
      this.$emit('undo', this.propsKey)
    },
  },
  watch: {
    memberPermission(v) {
      this.memberPermissions = v
    },
    memberPermissions(v) {
      this.$emit('update:memberPermission', v)

      if (v === 'SPECIFIC_MEMBER') {
        this.disabledSelect = false
      } else {
        this.disabledSelect = true
        // 지정 멤버를 제외한 메뉴 선택시, 지정 멤버 리스트를 비웁니다.
        this.selectMemberArray = []
      }
    },
    selectMembers(v) {
      this.selectMemberArray = v
      this.selectChange()
    },
    selectMemberArray(v) {
      this.$emit('update:selectMembers', v)
    },
  },
  mounted() {
    this.disabledSelect = this.disableState
    this.setLabelImage()
  },
}
</script>

<style lang="scss">
#__nuxt .project-info-modal .el-dialog__body .project-member-select {
  .el-row {
    margin: 12px 0;
    .el-button {
      position: absolute;
      right: 0;
      width: 24px;
      height: 24px;
      padding: 0;
    }
  }
  .virnect-workstation-form {
    .input-placeholder {
      position: absolute;
      z-index: 1;
      margin: 8px 0 8px 12px;
      cursor: var(--cursor);
      pointer-events: var(--pointer);

      &:hover + .select-member {
        background-color: #e6e9ee;
      }
      > span {
        color: #5e6b81;
        font-size: 13px;
        vertical-align: top;
      }
    }
  }

  .label-user {
    & div:nth-child(1) {
      display: inline-block;
      width: 100px;
      height: 24px;
      margin-top: 8px;
      background-color: #f5f7fa;
      border-radius: 12px;
      &:hover {
        background-color: #e3eeff;
      }

      .virnect-thumbnail {
        display: inline-block;
        width: 16px;
        height: 16px;
        margin: 0 0 0 8px;
        vertical-align: middle;
      }
      .delete {
        vertical-align: middle;
        cursor: pointer;
      }
      span {
        display: inline-block;
        width: 46px;
        margin-left: 4px;
        overflow-x: hidden;
        font-weight: 500;
        font-size: 12px;
        white-space: nowrap;
        text-overflow: ellipsis;
        vertical-align: middle;
      }
    }
    div:nth-child(2) {
      display: inline-block;
      margin-left: 4px;
      font-weight: 500;
      font-size: 12px;
    }
  }
}
.select-member__list {
  .el-select-dropdown__item {
    color: black;
    .virnect-thumbnail {
      display: inline-block;
      margin: 0 12px 0 4px;
      vertical-align: middle;
    }
  }
}
</style>
