<template>
  <div class="project-member-select">
    <el-divider />
    <!-- 프로젝트 공유 / 편집 정보 -->
    <dl class="select" ref="form">
      <dt>{{ $t(selectLabel) }}</dt>
      <dd class="virnect-workstation-form">
        <el-select v-model="memberTypes">
          <el-option
            v-for="type in selectTypes"
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
          v-model="memberArray"
          name="input"
          multiple
          collapse-tags
          placeholder=""
          @change="selectChange(memberArray)"
          :disabled="disabledSelect"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
            <!-- 유저 프로필 유저 닉네임 -->
            <VirnectThumbnail :size="22" :image="item.img" />
            <span>{{ item.value }}</span>
          </el-option>
        </el-select>

        <el-button
          type="is-disabled"
          @click="$emit('update:updateProjectMember', propsKey)"
        >
          {{ $t('common.apply') }}
        </el-button>

        <!-- 지정 유저 라벨 -->
        <div v-if="members.length > 0" class="label-user">
          <el-tooltip
            effect="dark"
            :content="members[0]"
            placement="right-start"
          >
            <div>
              <VirnectThumbnail
                :image="labelImg"
                :defaultImage="$defaultUserProfile"
              />
              <span>{{ members[0] }}</span>
              <img
                @click="deleteFirstUser"
                class="delete"
                src="~assets/images/icon/ic-close.svg"
              />
            </div>
          </el-tooltip>

          <div v-if="members.length > 1">+ {{ members.length - 1 }}</div>
        </div>
      </dd>
    </dl>
  </div>
</template>

<script>
export default {
  props: {
    selectLabel: String,
    selectTypes: [Array],
    memberType: String,
    members: [Array],
    options: [Array],
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
      memberTypes: this.memberType,
      // 지정 멤버로 선택한 유저 리스트
      memberArray: this.members,
    }
  },

  computed: {
    cssVars() {
      return {
        '--cursor': this.disabledSelect ? 'not-allowed' : 'pointer',
        '--pointer': this.disabledSelect ? 'all' : 'none',
      }
    },
  },
  methods: {
    // 지정 멤버 드롭다운 메뉴에서 유저 선택시, 실행하는 메소드
    selectChange(choiceMembers) {
      // 선택한 유저 수가 0명 이상일 때,
      if (choiceMembers.length) {
        // 선택된 유저 리스트에서 첫 유저의 프로필 사진을 대표 라벨 이미지로 보여주기
        this.labelImg = this.options.filter(
          a => a.value === choiceMembers[0],
        )[0].img
      }
    },
    // 선택한 지정 멤버 리스트에서, 첫 번째 인덱스 멤버를 삭제한다. 라벨의 X 버튼 클릭시 실행.
    deleteFirstUser() {
      if (!this.memberArray.length) {
        return null
      }
      // key 값을 통해, 공유 / 편집 members 를 구분.
      this.memberArray.shift()
    },
  },
  watch: {
    memberTypes(v) {
      this.$emit('update:memberType', v)
      if (v === 'SELECTMEMBER') {
        this.disabledSelect = false
      } else {
        this.disabledSelect = true
      }
    },
    memberArray(v) {
      this.$emit('update:members', v)
    },
  },
}
</script>

<style lang="scss">
.project-member-select {
  .virnect-workstation-form {
    .input-placeholder {
      position: absolute;
      margin: 8px 0 8px 12px;
      z-index: 1;
      cursor: var(--cursor);
      pointer-events: var(--pointer);

      &:hover + .select-member {
        background-color: #e6e9ee;
      }
      > span {
        font-size: 13px;
        vertical-align: top;
        color: #5e6b81;
      }
    }
  }

  .label-user {
    & div:nth-child(1) {
      display: inline-block;
      border-radius: 12px;
      width: 100px;
      height: 24px;
      margin-top: 8px;
      background-color: #f5f7fa;
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
        margin-left: 4px;
        font-size: 12px;
        font-weight: 500;
        vertical-align: middle;
        display: inline-block;
        width: 46px;
        overflow-x: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    div:nth-child(2) {
      display: inline-block;
      margin-left: 4px;
      font-size: 12px;
      font-weight: 500;
    }
  }
}
.select-member__list {
  .el-select-dropdown__item {
    color: black;
    .virnect-thumbnail {
      display: inline-block;
      vertical-align: middle;
      margin: 0 12px 0 4px;
    }
  }
}
</style>
