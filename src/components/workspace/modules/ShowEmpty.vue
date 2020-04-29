<template>
  <div class="show-empty">
    <img class="show-empty__image" :src="image" />
    <p class="show-empty__title">
      {{ title }}
    </p>
    <p class="show-empty__description">
      {{ description }}
    </p>
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

.show-empty {
  position: absolute;
  width: 100%;
  height: 100%;
  background: $color_darkgray_600;
}
.show-empty__image {
  position: absolute;
  top: 2.143rem;
  left: 50%;
  transform: translateX(-50%);
}
.show-empty__title {
  padding-top: 21.429rem;
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
</style>
