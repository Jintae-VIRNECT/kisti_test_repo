<template>
  <popover
    placement="bottom-end"
    width="92"
    trigger="click"
    popperClass="popover-language"
    :scrollHide="true"
  >
    <button class="language-button" slot="reference"></button>
    <div>
      <div class="popover-language__button">
        <button @click="changeLange('ko')">
          KOREA
        </button>
      </div>
      <div class="popover-language__button">
        <button @click="changeLange('en')">
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
  methods: {
    changeLange(lang) {
      console.log(lang)
      this.$nextTick(() => {
        this.$eventBus.$emit('popover:close')
      })
    },
  },
}
</script>

<style lang="scss">
.language-button {
  width: 2.4286rem;
  height: 2.4286rem;
  background: url('~assets/image/ic_language.svg') 50% no-repeat;
}

.popover-language {
  min-width: 6.7143rem;
  height: 6.5714rem;
  background: rgb(255, 255, 255);
  > .popover--body {
    padding: 7px 0;
  }
}

.popover-language__button {
  width: 100%;
  height: 2.7143rem;
  padding: 0.5714rem 0;
  text-align: center;

  &:hover {
    background: #e3e7ed;
  }

  > button {
    width: 100%;
    color: #0b1f48;
    font-weight: 500;
    font-size: 1.0714rem;
    background: transparent;

    &:active {
      font-weight: 500;
    }
  }
}
</style>
