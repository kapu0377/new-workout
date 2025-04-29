async function get1(eno){
    const result = await axios.get(`/exercise/replies/read/${eno}`)
    return result
}

async function getList({eno,page,size,goLast}){
    const result = await axios.get(`/exercise/replies/read/${eno}`, {params:{page,size}})
    if(goLast){
        const total = result.data.total
        const lastPage = parseInt(Math.ceil(total/size))
        return getList({eno:eno, page:lastPage, size:size})
    }
    return result.data
}

async function addReply(replyObj){
    const response = await axios.post(`/exercise/replies/`,replyObj)
    return response.data
}

async function getReply(rno){
    const response = await axios.get(`/exercise/replies/${rno}`)
    return response.data
}

async function modifyReply(replyObj){
    const response = await axios.put(`/exercise/replies/${replyObj.rno}`,replyObj)
    return response.data
}

async function removeReply(rno){
    const response = await axios.delete(`/exercise/replies/${rno}`)
    return response.data
}