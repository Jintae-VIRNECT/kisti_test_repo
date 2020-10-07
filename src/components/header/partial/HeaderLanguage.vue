<template>
  <popover
    placement="bottom-end"
    trigger="click"
    width="92"
    popperClass="popover-language"
  >
    <button class="language-button" slot="reference"></button>
    <div>
      <div class="popover-language__button">
        <button>
          KOREA
        </button>
      </div>
      <div class="popover-language__button">
        <button>
          ENGLISH
        </button>
      </div>
    </div>
  </popover>
</template>

<script>
import Popover from 'Popover'
export default {
  name: 'HeaderLanguage',
  components: {
    Popover,
  },
}
</script>

<style lang="scss">
.language-button {
  width: 34px;
  height: 34px;
  background: url('~assets/image/ic_language.svg') 50% no-repeat;
}

.popover-language {
  width: 100%;
  min-width: 94px;
  height: 92px;
  background: rgb(255, 255, 255);
  > .popover--body {
    padding: 7px 0;
  }
}

.popover-language__button {
  width: 100%;
  height: 38px;
  padding: 8px 0;
  text-align: center;

  &:hover {
    background: #e3e7ed;
  }

  > button {
    width: 100%;
    color: #0b1f48;
    font-weight: 500;
    font-size: 15px;
    background: transparent;

    &:active {
      font-weight: 500;
    }
  }
}
</style>
