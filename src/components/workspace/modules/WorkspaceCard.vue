<template>
  <card class="workcard" width="100%" height="27.071em">
    <div class="workcard-body">
      <div class="workcard-profile">
        <div class="profile__image">
          <profile
            :group="true"
            :image="workspaceInfo.profile"
            :thumbStyle="{ width: '5.143rem', height: '5.143rem' }"
          ></profile>
        </div>
        <p class="profile__name">{{ workspaceInfo.title }}</p>
      </div>
      <button class="workcard-button btn small" @click="join">
        {{ $t('button.select') }}
      </button>
    </div>
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
