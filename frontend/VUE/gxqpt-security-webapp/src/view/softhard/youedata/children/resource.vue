<!-- 资源需求配置 -->
<template>
    <div class="ivu-layout serviceDetail">
        <h2>资源需求配置</h2>
        <specificInfo style="margin-top:30px;"></specificInfo>
        <Row class="bottom-bar" style="text-align: center;background:#fff;">
            <Col>
                <div class="btnList">
                    <Button type="default" @click="goback(-2)">取消</Button>
                    <Button type="primary" @click="goback(-1)">上一步</Button>
                    <Button type="primary" @click="submit">提交</Button>
                </div>
            </Col>
        </Row>
    </div>
</template>

<script>
import specificInfo from './../../common/specificInfo'
export default {
    data(){
        return{
        }
    },
    methods:{
        goback(n){
            this.$router.go(n)
        },
        submit(){
            console.log("提交");
        }
    },
    components: {
        specificInfo,
    },
}
</script>

<style lang='less' scoped>
.serviceDetail{
    background:#eee;
    padding: 20px;
    width:80%;
    height:auto;
}
.bottom-bar{
    margin-top:0;
}
.btnList{
    padding:10px;
    button{
        margin:0 20px;
    }
}
</style>
