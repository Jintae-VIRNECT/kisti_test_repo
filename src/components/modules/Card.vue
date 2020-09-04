<template>
  <div class="card">
    <div class="card-header">
      <h5 class="card-header--category" v-if="category">{{ category }}</h5>
      <h2 class="card-header--title" v-if="title">{{ title }}</h2>
    </div>
    <div class="card-body">
      <!-- <scroller height="200px"> -->
      <slot></slot>
      <!-- </scroller> -->
    </div>
    <div class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script>
// import Scroller from './Scroller'
export default {
  name: 'Card',
  components: {
    // Scroller,
  },
  data() {
    return {}
  },
  props: {
    title: {
      type: String,
      default: null,
    },
    category: {
      type: String,
      default: null,
    },
  },
}
</script>

<style lang="scss">
.card {
  position: relative;
  height: 100%;
  margin-bottom: 30px;
  padding: 15px;
  background: #27293d;
  border: 0;
  border-radius: 0.2857rem;
  box-shadow: 0 1px 20px 0px rgba(0, 0, 0, 0.1);
}
.card-header--category {
  margin-bottom: 5px;
  color: #9a9a9a;
  font-weight: 300;
  font-size: 10.5px;
}
.card-header--title {
  color: #ffffff;
  font-weight: 100;
  font-size: 1.6875rem;
}
</style>
