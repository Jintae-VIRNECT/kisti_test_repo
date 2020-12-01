<template>
  <nav class="header-workspace-lnb">
    <lnb-selector></lnb-selector>
  </nav>
</template>

<script>
import LnbSelector from '../tools/LnbSelector'
export default {
  name: 'HeaderNav',
  components: {
    LnbSelector,
  },
}
</script>

<style lang="scss">
.header-workspace-lnb {
  position: relative;
  width: 38.5714rem;
  height: 2.8571rem;
  margin: auto 0;
  text-align: left;
  background: #424242;
  border: 1px solid #5d5d5d;
  border-radius: 8px;
  .popover--wrapper {
    height: 100%;
  }
  .header-workspace-selector {
    position: relative;
    width: 38.5714rem;
    height: 100%;
    padding: 0 1.786rem 0 1.429rem;
    color: #dcdcdc;
    font-weight: normal;
    font-size: 1.0714rem;
    text-align: left;
    background: transparent;

    &::after {
      position: absolute;
      top: 0.6429rem;
      right: 0.8571rem;
      width: 20px;
      height: 20px;
      background: url(~assets/image/ic_dropdown.svg) 50% no-repeat;
      border-radius: 4px;
      content: '';
    }
  }
}
</style>
