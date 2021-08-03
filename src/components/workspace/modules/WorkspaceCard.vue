<template>
  <card class="workcard" width="100%" height="27.071em">
    <div class="workcard-body">
      <div class="workcard-profile">
        <div class="profile__image">
          <profile
            :group="true"
            :image="workspaceInfo.profile"
            :thumbStyle="thumbStyle"
          ></profile>
        </div>
        <p class="profile__name">{{ workspaceInfo.title }}</p>
      </div>
      <button class="workcard-button btn small" @click="join">
        {{ $t('button.select') }}
      </button>
    </div>
    <!--워크스페이스 선택 이벤트 적용되는 element 모바일 사이즈에서만 활성화됨-->
    <div class="workcard-select" @click="join"></div>
  </card>
</template>

<script>
import Card from 'Card'
import Profile from 'Profile'
import mixinToast from 'mixins/toast'

export default {
  name: 'RemoteCard',
  mixins: [mixinToast],
  components: {
    Card,
    Profile,
  },
  props: {
    thumbStyle: {
      type: Object,
      default: () => {
        return {}
      },
    },
    workspaceInfo: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  methods: {
    join() {
      this.$emit('join')
    },
  },
  /* Lifecycles */
  mounted() {},
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-workcard.scss"
></style>
