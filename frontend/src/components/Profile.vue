<template>
  <div class="container">
    <header class="jumbotron">
      <h3>
        <p>User Info</p>
      </h3>
    </header>
    <div>
      <h5>
        <pre>{{ content | pretty }}</pre>
      </h5>
    </div>
  </div>
</template>

<script>

  export default {
    name: 'profile',
    data() {
      return {
        content: ''
      }
    },
    mounted() {
      // UserService.getUserContent().then(
      //   response => {
      //     this.content = response.data;
      //   },
      //   error => {
      //     this.content = error.response.data.message;
      //   }
      // )
      this.content = JSON.parse(localStorage.getItem('user'));
    },
    methods: {
      pretty(value) {
        return JSON.stringify(JSON.parse(value), null, 2);
      }
    }
  };
</script>
