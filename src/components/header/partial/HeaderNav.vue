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
  width: 45.7143rem;
  height: 2.9286rem;
  margin: auto 0;
  text-align: left;
  background: #424242;
  border: 1px solid #5d5d5d;
  border-radius: 8px;
  .popover--wrapper {
    width: 100%;
    height: 100%;
  }
  .header-workspace-selector {
    position: relative;
    width: 100%;
    height: 100%;
    padding: 0 1.786rem 0 42px;

    overflow: hidden;
    color: #dcdcdc;
    font-weight: normal;
    font-size: 1.0714rem;
    white-space: nowrap;
    text-align: left;
    text-overflow: ellipsis;
    background: transparent;

    &.active {
      outline: none;
      outline-offset: 0;
      &::after {
        transform: rotate(-180deg);
        content: '';
      }
    }

    &::before {
      position: absolute;
      top: 0.5715rem;
      left: 0.7857rem;
      width: 1.7143rem;
      height: 1.7143rem;
      background: url(~assets/image/ic_workspace.svg) 50% no-repeat;
      content: '';
    }

    &::after {
      position: absolute;
      top: 0.6429rem;
      right: 0.8571rem;
      width: 1.4286rem;
      height: 1.4286rem;
      background: url(~assets/image/ic_dropdown.svg) 50% no-repeat;
      border-radius: 4px;
      transition: transform 0.3s;
      content: '';
    }
  }
}
</style>
