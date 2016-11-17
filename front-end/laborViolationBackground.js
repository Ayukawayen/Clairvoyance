const apiKey = 'AIzaSyBWqRdg-282o5dypBBI0qqz8I1MOT5HiZg';
const apiUri = 'https://sheets.googleapis.com/v4/spreadsheets';
const ssId = '1uLR9eFePzLzlnkO1k1yh2-2_TJCOYGGEatGCQOgpz9M';

let cpRecords = {};

refreshLaborViolation();
setInterval(refreshLaborViolation, 7200000);

chrome.runtime.onMessage.addListener(
    (request, sender, sendResponse) => {
        if(request.message == 'listLaborViolationRecord') {
            sendResponse(cpRecords);
            return true;
        }
    }
);


function refreshLaborViolation() {
    let range = "'索引'!A2:D";
    
    let uri = `${apiUri}/${ssId}/values/${range}?valueRenderOption=FORMULA&key=${apiKey}`;
    let regex = /=HYPERLINK\(\"(.*)\",\"(.*)\"\)/;
    
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if(this.readyState !== 4) return;
        if(this.status !== 200) return;
        
        let result = JSON.parse(this.responseText);
        if(!result || !result.values || !result.values.forEach) return;
        
        let sheetInfos = {};
        result.values.forEach((item) => {
            let matches = regex.exec(item[0]);
            sheetInfos[(matches[2]||item[0])] = {
                url:matches[1]||null,
                desc:item[1]||'',
                lastModified:new Date(item[2]||0),
                lastCheck:new Date(item[3]||0),
            }
        });
        
        refreshRecords(sheetInfos);
    };
    xhr.open('get', uri);
    xhr.send('');
};

function refreshRecords(sheetInfos) {
    let uri = `${apiUri}/${ssId}/values:batchGet?key=${apiKey}`;
    for(let k in sheetInfos) {
        uri += `&ranges='${k}'!A1:E`
    }
    let regex = /^'(.+)'!/;
    
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if(this.readyState !== 4) return;
        if(this.status !== 200) return;
        
        let result = JSON.parse(this.responseText);
        if(!result || !result.valueRanges || !result.valueRanges.forEach) return;
        
        cpRecords = {};
        
        result.valueRanges.forEach((valueRange)=>{
            let name = regex.exec(valueRange.range)[1];

            valueRange.values.forEach((item)=>{
                cpRecords[item[0]] = cpRecords[item[0]] || [];
                cpRecords[item[0]].push({
                    資料集:name,
                    url:sheetInfos[name].url,
                    條款:item[1],
                    內容:item[2],
                    文號:item[3],
                    日期:item[4],
                });
            });
        });
        
        for(let cpName in cpRecords) {
            cpRecords[cpName].sort((a,b) => ((a.日期==b.日期) ? (a.條款>b.條款 ? 1 : -1) : (a.日期>b.日期 ? -1 : 1)));
        }
    };
    xhr.open('get', uri);
    xhr.send('');
};
