<template>
  <div class="show-empty">
    <div class="show-empty__inner">
      <img class="show-empty__image" :src="image" />
      <p class="show-empty__title">
        {{ title }}
      </p>
      <p class="show-empty__description">
        {{ description }}
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ShowEmpry',
  components: {},
  data() {
    return {}
  },
  props: {
    image: {
      type: String,
      default: '',
    },
    title: {
      type: String,
      default: '',
    },
    description: {
      type: String,
      default: '',
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
@import '~assets/style/mixin';

.show-empty {
  position: absolute;
  display: flex;
  width: 100%;
  height: 100%;
  background: $color_darkgray_600;
}
.show-empty__inner {
  margin: auto;
  text-align: center;
}
.show-empty__image {
  max-width: 37.786rem;
  // position: absolute;
  // top: 2.143rem;
  // left: 50%;
  // transform: translateX(-50%);
}
.show-empty__title {
  padding-top: 1rem;
  color: $color_text_sub;
  font-size: 1.714rem;
  text-align: center;
}
.show-empty__description {
  padding: 0.357rem 0 2rem;
  color: rgba($color_text_sub, 0.5);
  font-weight: 300;
  font-size: 1.286rem;
  text-align: center;
}

@include responsive-mobile {
  .show-empty__title {
    @include fontLevel(200);
    color: $new_color_text_main;
  }
  .show-empty__description {
    @include fontLevel(75);
    padding-top: 0.2rem;
    color: $new_color_text_sub_description;
  }
}
</style>
