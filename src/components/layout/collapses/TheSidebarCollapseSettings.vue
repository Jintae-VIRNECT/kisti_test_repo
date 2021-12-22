<template>
  <div class="the-sidebar__collapse--default">
    <div class="the-sidebar__collapse__header">
      <img src="~assets/images/icon/ic-setting.svg" />
      <h5>{{ $t('menu.workspaceSetting') }}</h5>
    </div>
    <div class="the-sidebar__collapse__body">
      <nuxt-link to="/workspace/setting" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.settings.info') }}</span>
      </nuxt-link>
      <nuxt-link
        v-if="isRoleManager"
        to="/onpremise/download"
        @click.native="$emit('closeCollapse')"
      >
        <span>{{ $t('menu.collapse.settings.download') }}</span>
      </nuxt-link>
    </div>
  </div>
</template>
<script>
import permissionMixin from '@/mixins/permission'
export default {
  mixins: [permissionMixin],
}
</script>
