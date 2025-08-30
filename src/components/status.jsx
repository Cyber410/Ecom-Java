const Status = ({ text, icon: Icon, bg, color }) => {
  return (
    <div className={`${bg} ${color} px-2 py-2 rounded flex flex-row-reverse justify-end items-end w-fit`}>
  {text} <Icon size={15}/>
</div>
  );
};

export default Status;