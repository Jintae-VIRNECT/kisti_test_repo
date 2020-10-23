<template>
  <div class="header-tools">
    <header-language></header-language>
    <header-profile></header-profile>
  </div>
</template>

<script>
import HeaderLanguage from './HeaderLanguage'
import HeaderProfile from './HeaderProfile'
export default {
  name: 'HeaderTools',
  components: {
    HeaderLanguage,
    HeaderProfile,
  },
}
</script>

<style lang="scss">
.header-tools {
  // position: absolute;
  // right: 22.8571rem;
  display: flex;
  // margin-left: 250px;
  padding: 1.1429rem;

  > .tooltip,
  > .popover--wrapper {
    margin-left: 2.1429rem;
    > .profile {
      margin: 0rem 0.143rem 0.143rem 0.929rem;
      cursor: pointer;
      &:hover {
        opacity: 0.8;
      }
    }
  }
}
</style>
