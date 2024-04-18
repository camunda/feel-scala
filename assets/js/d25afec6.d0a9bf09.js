"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[6273],{4863:(e,t,n)=>{n.d(t,{Z:()=>s});var l=n(7462),a=n(7294),r=n(5671),o=n(3746);const s=e=>{let{children:t,onChange:n,language:s}=e;const i=(0,a.useRef)(null),[u,c]=(0,a.useState)(t),p=(0,a.useCallback)((e=>{const t=e.slice(0,-1);c(t),n(t)}),[]);return(0,r.Y)(i,p,{indentation:2}),a.createElement(o.ZP,(0,l.Z)({},o.lG,{code:u,language:s}),(e=>{let{className:t,style:n,tokens:l,getTokenProps:r}=e;return a.createElement("pre",{className:t,style:n,ref:i},l.map(((e,t)=>a.createElement(a.Fragment,{key:t},e.filter((e=>!e.empty)).map(((e,t)=>a.createElement("span",r({token:e,key:t})))),"\n"))))}))}},4294:(e,t,n)=>{n.d(t,{Z:()=>i});var l=n(7294),a=n(9669),r=n.n(a),o=n(4863),s=n(9537);const i=e=>{let{defaultExpression:t,feelContext:n,metadata:a,onResultCallback:i,onErrorCallback:u}=e;n&&(n=JSON.stringify(JSON.parse(n),null,2));const[c,p]=l.useState(t),[d,m]=l.useState(n),[h,g]=l.useState("<click 'Evaluate' to see the result of the expression>"),[f,k]=l.useState(null),[v,E]=l.useState(null),C=/^.+(?<line>\d+):(?<position>\d+).+$/gm,y=/^.+at position (?<position>\d+)$/gm;function b(e,t){g(null),k(e),E(t),u&&u(e)}return l.createElement("div",null,l.createElement("h2",null,"Expression"),l.createElement(o.Z,{onChange:p,language:"js"},c),n&&l.createElement("div",null,l.createElement("h2",null,"Context"),l.createElement("i",null,"A JSON document that is used to resolve ",l.createElement("strong",null,"variables")," ","in the expression."),l.createElement(o.Z,{onChange:m,language:"json"},d)),l.createElement("button",{onClick:function(){try{g("<evaluating the expression...>"),E(null);!function(e){r().post("https://feel.upgradingdave.com/api/v1/feel/evaluate",{expression:c,context:e,metadata:{...a}},{headers:{accept:"*/*","content-type":"application/json"}}).then((e=>{var t;if(null!=e&&null!=(t=e.data)&&t.error){var n,l;const t=e.data.error,a=C.exec(t);b({message:t,line:null==a||null==(n=a.groups)?void 0:n.line,position:null==a||null==(l=a.groups)?void 0:l.position},e.data.warnings)}else!function(e){k(null);const t=JSON.stringify(e.result);g(t),e.warnings.length>=1&&E(e.warnings);i&&i(t)}(e.data)}))}(n&&0!==d.trim().length?JSON.parse(d):{})}catch(t){var e;const n=y.exec(t.message);b({message:"failed to parse context: "+t.message,position:null==n||null==(e=n.groups)?void 0:e.position})}},className:"button button--primary button--lg"},"Evaluate"),l.createElement("br",null),l.createElement("br",null),l.createElement("h2",null,"Result"),l.createElement(s.Z,{title:(()=>{const e=null!=f&&f.line?" on line "+f.line:"",t=null!=f&&f.position?" at position "+f.position:"";return f&&"Error"+e+t})(),language:"json"},h||(null==f?void 0:f.message)),l.createElement("br",null),l.createElement("h2",null,"Warnings"),l.createElement(s.Z,null,(null==v?void 0:v.map(((e,t)=>l.createElement("li",{key:t},"[",e.type,"] ",e.message))))||"<none>"))}},3154:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>m,contentTitle:()=>p,default:()=>f,frontMatter:()=>c,metadata:()=>d,toc:()=>h});var l,a=n(7462),r=n(1880),o=(n(7294),n(3905)),s=n(4294),i=n(6518),u=n.n(i);const c={id:"chapter-6",title:"Chapter 6"},p=void 0,d={unversionedId:"learn/challenge/chapter-6",id:"version-1.16/learn/challenge/chapter-6",title:"Chapter 6",description:"Zee took a short rest and continued walking to CamundaCon. On the road, he passed mountains,",source:"@site/versioned_docs/version-1.16/learn/challenge/chapter-6.mdx",sourceDirName:"learn/challenge",slug:"/learn/challenge/chapter-6",permalink:"/feel-scala/docs/1.16/learn/challenge/chapter-6",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/versioned_docs/version-1.16/learn/challenge/chapter-6.mdx",tags:[],version:"1.16",frontMatter:{id:"chapter-6",title:"Chapter 6"},sidebar:"Learn",previous:{title:"Chapter 5",permalink:"/feel-scala/docs/1.16/learn/challenge/chapter-5"}},m={},h=[],g={toc:h};function f(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,a.Z)({},g,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"Zee took a short rest and continued walking to CamundaCon. \ud83c\udde9\ud83c\uddea On the road, he passed mountains,\nfields, and woods. \u26f0\ufe0f \ud83c\udf33 After a few days, he realized that he walked in cycles.\n\ud83d\ude35 Unsure about right way, he looks at the trail markers. \ud83d\udfe5\n\ud83d\udd35 \ud83d\udd36"),(0,o.kt)("p",null,"Can you help him to choose the right way to Berlin?"),(0,o.kt)(s.Z,{defaultExpression:u()(l||(l=(0,r.Z)(["\n    // use list operators and return the right route\n    routes"]))),feelContext:'{"routes": [ {"route":"red", "stops":["Cologne", "Frankfurt", "Nuremberg", "Munich"]}, {"route":"blue", "stops":["Cologne", "Paderborn", "Hanover", "Berlin"]}, {"route":"yellow", "stops":["Cologne", "M\xfcnster", "Bremen", "Hamburg"]}]}',metadata:{page:"challenge-6"},mdxType:"LiveFeel"}),(0,o.kt)("details",null,(0,o.kt)("summary",null,"Solution"),(0,o.kt)("div",null,(0,o.kt)("div",null,"Zee follows the blue route and reaches CamundaCon just-in-time. He is happy to be there and meet the community."),(0,o.kt)("br",null),(0,o.kt)("pre",{title:"Expression"},'routes["Berlin" in stops][1].route'))))}f.isMDXComponent=!0}}]);