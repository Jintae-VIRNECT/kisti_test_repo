<template>
  <div class="dataview">
    <div class="dataview-header">
      <search-filter></search-filter>
      <button class="dataview-header__button list" @click="view('list')">
        List View
      </button>
      <button class="dataview-header__button card" @click="view('card')">
        Card View
      </button>
    </div>
    <div class="dataview-body">
      <p class="dataview-body__title">
        <slot name="title"></slot>
      </p>
      <p class="dataview-body__description">
        <slot name="description"></slot>
      </p>
      <div class="listview" v-if="viewMode === 'list'">
        <template v-for="card of list">
          <slot name="listview" v-bind:card="card"></slot>
        </template>
      </div>
      <div class="cardview" v-else>
        <template v-for="card of list">
          <slot name="cardview" v-bind:card="card"></slot>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
// import ListView from 'ListView'
// import CardView from 'CardView'
// import Card from 'Card'
import SearchFilter from 'SearchFilter'

export default {
  name: 'DataView',
  components: {
    // ListView,
    // CardView,
    // Card,
    SearchFilter,
  },
  props: {
    list: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {
      viewMode: 'card',
    }
  },
  computed: {},
  methods: {
    view(value) {
      this.viewMode = value
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.dataview {
  position: relative;
  width: 100%;
  height: 100px;
}
.dataview-header {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  height: 36px;
}
.dataview-header__button {
  width: 30px;
  height: 30px;
  margin-left: 5px;
  background-color: transparent;
  @include ir();

  &.list {
    background: url(~assets/image/material_view_list.png) 50%/30px no-repeat;
  }
  &.card {
    background: url(~assets/image/material_view_module.png) 50%/30px no-repeat;
  }
}

.dataview-body {
  position: relative;
  top: 36px;
  min-height: 100px;
  border: solid 1px #393939;
}
.dataview-body__title {
  margin-top: 24px;
  color: #d2d2d2;
  font-weight: 500;
  font-size: 15px;
}
.dataview-body__description {
  margin-top: 12px;
  color: #d2d2d2;
  opacity: 0.62;
}
.listview {
  margin-top: 30px;
  > .card {
    margin-bottom: 10px;
    padding: 22px 30px;
  }
}
.listview-body {
  display: flex;
  width: 100%;
  height: 100%;
}
.listview-profile {
  width: 42px;
  height: 42px;
  margin: auto 0;
}

.cardview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  margin-top: 30px;
  > .card {
  }
}
.cardview-body {
  display: flex;
  width: 100%;
  height: 100%;
}
.cardview-profile {
  width: 64px;
  height: 64px;
  margin: 0 auto;
}
</style>
