"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[336],{5478:(e,t,n)=>{n.d(t,{Z:()=>p});var a=n(7294),l=n(9669),o=n.n(l),r=n(7462),s=n(5671),i=n(3746);const u=e=>{let{children:t,onChange:n,language:l}=e;const o=(0,a.useRef)(null),[u,c]=(0,a.useState)(t),p=(0,a.useCallback)((e=>{const t=e.slice(0,-1);c(t),n(t)}),[]);return(0,s.Y)(o,p,{indentation:2}),a.createElement(i.ZP,(0,r.Z)({},i.lG,{code:u,language:l}),(e=>{let{className:t,style:n,tokens:l,getTokenProps:r}=e;return a.createElement("pre",{className:t,style:n,ref:o},l.map(((e,t)=>a.createElement(a.Fragment,{key:t},e.filter((e=>!e.empty)).map(((e,t)=>a.createElement("span",r({token:e,key:t})))),"\n"))))}))};var c=n(9537);const p=e=>{let{defaultExpression:t,feelContext:n,metadata:l,onResultCallback:r,onErrorCallback:s}=e;n&&(n=JSON.stringify(JSON.parse(n),null,2));const[i,p]=a.useState(t),[d,m]=a.useState(n),[g,f]=a.useState("<click 'Evaluate' to see the result of the expression>"),[h,k]=a.useState(null),E=/^.+(?<line>\d+):(?<position>\d+).+$/gm,b=/^.+at position (?<position>\d+)$/gm;function y(e){f(null),k(e),s&&s(e)}return a.createElement("div",null,a.createElement("h2",null,"Expression"),a.createElement(u,{onChange:p,language:"js"},i),n&&a.createElement("div",null,a.createElement("h2",null,"Context"),a.createElement("i",null,"A JSON document that is used to resolve ",a.createElement("strong",null,"variables")," ","in the expression."),a.createElement(u,{onChange:m,language:"json"},d)),a.createElement("button",{onClick:function(){try{!function(e){o().post("https://feel.upgradingdave.com/process/start",{expression:i,context:e,metadata:{...l}},{headers:{accept:"*/*","content-type":"application/json"}}).then((e=>{var t,n;if(null!=e&&null!=(t=e.data)&&t.result)!function(e){k(null),f(e),r&&r(e)}(JSON.stringify(e.data.result));else if(null!=e&&null!=(n=e.data)&&n.error){var a,l;const t=e.data.error,n=E.exec(t);y({message:t,line:null==n||null==(a=n.groups)?void 0:a.line,position:null==n||null==(l=n.groups)?void 0:l.position})}}))}(n?JSON.parse(d):{})}catch(t){var e;const n=b.exec(t.message);y({message:"failed to parse context: "+t.message,position:null==n||null==(e=n.groups)?void 0:e.position})}},className:"button button--primary button--lg"},"Evaluate"),a.createElement("br",null),a.createElement("br",null),a.createElement("h2",null,"Result"),a.createElement(c.Z,{title:(()=>{const e=null!=h&&h.line?" on line "+h.line:"",t=null!=h&&h.position?" at position "+h.position:"";return h&&"Error"+e+t})(),language:"json"},g||(null==h?void 0:h.message)))}},7513:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>m,contentTitle:()=>p,default:()=>h,frontMatter:()=>c,metadata:()=>d,toc:()=>g});var a,l=n(7462),o=n(1880),r=(n(7294),n(3905)),s=n(5478),i=n(6518),u=n.n(i);const c={id:"tutorial",title:"The quest begins"},p=void 0,d={unversionedId:"tutorial/tutorial",id:"tutorial/tutorial",title:"The quest begins",description:"The tutorial is created as part of our Camunda Summer Hack Days project 2022.",source:"@site/docs/tutorial/tutorial.mdx",sourceDirName:"tutorial",slug:"/tutorial/",permalink:"/feel-scala/docs/tutorial/",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/master/docs/docs/tutorial/tutorial.mdx",tags:[],version:"current",frontMatter:{id:"tutorial",title:"The quest begins"},sidebar:"Tutorial",next:{title:"1.1 Numeric expressions",permalink:"/feel-scala/docs/tutorial/tutorial-1-1"}},m={},g=[],f={toc:g};function h(e){let{components:t,...i}=e;return(0,r.kt)("wrapper",(0,l.Z)({},f,i,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("admonition",{title:"Work in progress",type:"danger"},(0,r.kt)("p",{parentName:"admonition"},"The tutorial is created as part of our Camunda Summer Hack Days project 2022."),(0,r.kt)("p",{parentName:"admonition"},"It may be incomplete, wrong, or broken. But stay tuned for updates!")),(0,r.kt)("p",null,"Welcome to our tutorial. \ud83d\udc4b"),(0,r.kt)("p",null,"We'll do our best to guide you through the different capabilities\nof ",(0,r.kt)("a",{parentName:"p",href:"/feel-scala/docs/reference/what-is-feel"},"FEEL")," and hopefully make the\nprocess fun. \ud83c\udf89"),(0,r.kt)("p",null,'We are enlisting you to help us guide our friend "Zee" to complete a quest from Spain \ud83c\uddea\ud83c\uddf8 to Berlin\n\ud83c\udde9\ud83c\uddea in time for CamundaCon (2022). With the use of FEEL we\'ll be able to help in the journey.'),(0,r.kt)("p",null,'Before we start, let\'s say "Hi" to Zee:'),(0,r.kt)("p",null,(0,r.kt)("img",{alt:"Zee",src:n(5710).Z,width:"128",height:"128"})),(0,r.kt)("p",null,"Use the interactive editor below to evaluate the\n",(0,r.kt)("a",{parentName:"p",href:"/feel-scala/docs/reference/language-guide/feel-expressions-introduction"},"FEEL expression")," and greet Zee."),(0,r.kt)(s.Z,{defaultExpression:u()(a||(a=(0,o.Z)(['\n    "Hello Zee"\n    ']))),metadata:{page:"tutorial-the-quest-begins"},mdxType:"LiveFeel"}),(0,r.kt)("admonition",{type:"note"},(0,r.kt)("p",{parentName:"admonition"},"FEEL is an expression language. Compared to script languages or other complex programming\nlanguages, an expression language evaluates only a single expression.")),(0,r.kt)("p",null,"Zee is happy to have you on board. So, let the journey begin. \ud83d\ude80"))}h.isMDXComponent=!0},5710:(e,t,n)=>{n.d(t,{Z:()=>a});const a=n.p+"assets/images/zee-9a14b3cab50dd062332f9ca0333089fa.png"}}]);