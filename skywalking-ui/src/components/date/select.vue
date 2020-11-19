<template>
  <a-row class="seletDate" type="flex" justify="center" align="middle">
    <a-range-picker
      :ranges="pickop"
      :showTime="{ format: 'HH:mm:ss' }"
      format="YYYY/MM/DD HH:mm:ss"
      @change="changeDate"
      v-model="dateRange"
    />
  </a-row>
</template>
<style lang="scss" scoped>
  .seletDate {
    z-index: 1;
    width: 100%;
    height: 1rem;
    // line-height: 1.25rem;
    font-size: 0.2rem;
    //box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
    box-shadow: $plane_shadow;
    margin-bottom: 0.1rem;
  }
</style>
<script>
  import moment from 'moment';
  export default {
    name: 'selectDate',
    data() {
      return {
        pickop: {
          今天: [moment().startOf('day'), moment()],
          昨天: [
            moment()
              .startOf('day')
              .subtract(1, 'days'),
            moment()
              .endOf('day')
              .subtract(1, 'days'),
          ],
          一周: [
            moment()
              .startOf('day')
              .subtract(1, 'weeks'),
            moment(),
          ],
          两周: [
            moment()
              .startOf('day')
              .subtract(2, 'weeks'),
            moment(),
          ],
          一个月: [
            moment()
              .startOf('day')
              .subtract(1, 'months'),
            moment(),
          ],
          一年: [
            moment()
              .startOf('day')
              .subtract(1, 'years'),
            moment(),
          ],
        },
        dateRange: [this.parseTime(new Date().getTime() - 3600 * 1000 * 24), this.parseTime(new Date())],
      };
    },
    created() {
      this.changeDate();
    },
    methods: {
      changeDate() {
        this.$emit('changeDate', this.dateRange);
      },
    },
  };
</script>
