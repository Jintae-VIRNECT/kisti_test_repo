<template>
  <article class="guest-invite-input__container">
    <label :for="label" class="guest-invite-input__label">
      <!-- <img :src="iconImgPath" :alt="label" /> -->
      <span :style="imgStyle" :alt="label"></span>
      <p>{{ title }}</p>
    </label>

    <div class="guest-invite-input__input">
      <input
        v-model="input"
        class="input"
        :id="label"
        :ref="label"
        :aria-label="label"
        :readonly="isInputReadOnly"
        :placeholder="placeholder"
      />

      <a
        v-if="!useButton"
        class="btn not-primary"
        :href="linkHref"
        target="_blank"
      >
        {{ buttonText }}</a
      >
      <button
        v-else
        class="btn"
        :class="{ 'not-primary': !isButtonPrimaryColor }"
        @click="onButtonClick"
      >
        {{ buttonText }}
      </button>
    </div>
  </article>
</template>

<script>
export default {
  props: {
    label: {
      type: String,
    },
    iconImgPath: {
      type: String,
    },
    title: {
      type: String,
    },
    buttonText: {
      type: String,
    },
    linkHref: {
      type: String,
    },
    isButtonPrimaryColor: {
      type: Boolean,
      default: false,
    },
    isInputReadOnly: {
      type: Boolean,
      default: false,
    },
    inputText: {
      type: String,
    },
    placeholder: {
      type: String,
    },
    useButton: {
      type: Boolean,
    },
  },
  data() {
    return {
      input: this.inputText,
    }
  },
  computed: {
    imgStyle() {
      return this.isMobileSize
        ? `mask-image: url(${this.iconImgPath}); background-color: #616872;`
        : `background-image: url(${this.iconImgPath});`
    },
  },
  watch: {
    input(newVal) {
      this.$emit('update:inputText', newVal)
    },
  },
  methods: {
    onButtonClick() {
      this.$emit('buttonClick')
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';

.guest-invite-input__label {
  display: flex;
  align-items: center;
  margin-bottom: 0.7857rem;
  > span {
    width: 24px;
    height: 24px;
  }
  > p {
    margin-left: 0.5714rem;
  }
}

.guest-invite-input__input {
  display: flex;
  align-items: center;
  width: 100%;
  height: 3.4286rem;
  padding: 0.9286rem 0.428rem 0.9286rem 1.1429rem;
  color: #fff;
  font-size: 1em;
  background-color: #1a1a1b;
  border: solid 1px #303030;
  border-radius: 3px;
  &::placeholder {
    color: rgba(#979fb0, 0.7);
  }

  & > input {
    width: 100%;
    overflow-y: hidden;
    color: #fff;
    font-weight: normal;
    font-size: 1rem;
    letter-spacing: 0px;
    background-color: #1a1a1b;
    border: none;
    outline: none;
    resize: none;
  }
  // &::-webkit-autofill,
  // &:-internal-autofill-selected {
  //   background-color: #1a1a1b !important;
  // }
  &::placeholder {
    color: rgba(#979fb0, 0.7);
  }
  &:hover {
    border: solid 1px #585858;
  }
  &:focus-within {
    border: solid 1px $color_primary;
  }

  .btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 7rem;
    height: 100%;
    padding: 0;
    color: #ffffff;
    font-weight: 500;
    font-size: 0.9286rem;
    border-radius: 2px;

    &.not-primary {
      padding: 0;
      background: $new_color_bg_button_sub;
      border-radius: 2px;

      &:hover {
        background: #494c5b;
      }

      &:active {
        background-color: $color_darkgray_400;
      }
    }
  }

  input:-webkit-autofill,
  input:-webkit-autofill:hover,
  input:-webkit-autofill:focus,
  textarea:-webkit-autofill,
  textarea:-webkit-autofill:hover,
  textarea:-webkit-autofill:focus,
  select:-webkit-autofill,
  select:-webkit-autofill:hover,
  select:-webkit-autofill:focus {
    -webkit-text-fill-color: #fff;
    -webkit-box-shadow: 0 0 0px 1000px transparent inset;
    transition: background-color 5000s ease-in-out 0s;
  }
}

@include responsive-mobile {
  .guest-invite-input__label {
    > p {
      @include fontLevel(100);
      margin-left: 0.8rem;
    }
  }
  .guest-invite-input__input {
    height: unset;
    min-height: 4.8rem;
    padding: 0.6rem 0.6rem 0.6rem 1.6rem;
    background-color: $new_color_bg;
    border: none;

    &:hover {
      border: none;
    }
    &:focus-within {
      border: none;
    }

    > input {
      color: $new_color_text_main;
      background-color: $new_color_bg;
      @include fontLevel(100);
      &::placeholder {
        color: $new_color_text_sub_description;
      }
    }
    > .btn,
    .btn.not-primary {
      width: auto;
      min-width: 8.7rem;
      height: auto;
      padding: 0.8rem 1.5rem;
      white-space: unset;
      @include fontLevel(100);
    }
  }
}
</style>
