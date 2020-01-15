export default {
    data() {
        return {
            showShutter: false,
            _shutterID: 0
        }
    },
    methods: {
        shutterAction() {
            //셔터 효과
            this.showShutter = true;
            this._shutterID = setTimeout(()=>{
                this.showShutter = false;
                clearTimeout(this._shutterID);
            },200)
        }
    },
    
    /* Lifecycles */
    created() {
        this.$eventBus.$on('recapture', this.shutterAction)
    },
    beforeDestroy() {
        this.$eventBus.$off('recapture', this.shutterAction)
    }
}