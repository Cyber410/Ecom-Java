import { Button, Dialog, DialogPanel, DialogTitle,DialogBackdrop } from '@headlessui/react'
import { Divider } from '@mui/material';
import Status from './status';
import { MdClose, MdDone } from 'react-icons/md';


function  ProductViewModal ({open, setOpen, product, isAvailable}) {
  

  const {productId,productName,price,specialPrice,description,image,quantity,category}=product;

  const handelClickOpen=()=>{
    setOpen(true);
  }

  return (
    <>
      

      <Dialog open={open} as="div" className="relative z-10 " onClose={close} __demoMode>
         <DialogBackdrop className="fixed inset-0 transition-opacity" />
        <div className="fixed inset-0 z-10 w-screen overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <DialogPanel
                transition
                className="relative transform-overflow-hidden rounded-lg bg-white shadow-xl transition-all md:max-w-[620px] md:min-w-[620px] w-full p-6"
                >
                {image && (
                  <div>
                    <img src={image} alt={productName} className=" w-full h-64 object-cover mb-4 rounded"/>
                  </div>
                )}

                <div className="px-6 pt-10 pb-2">
                    <DialogTitle as="h3" className="lg:text-3xl sm:text-2xl text-xl font-semibold leading-6 text-gray-800 ,pb-6">
                        {productName}
                    </DialogTitle>
                    <div className="flex flex-row justify-between pt-2.5">
                                <div className="flex flex-col">
                                    {specialPrice ? (
                                    <div className="flex flex-col">
                                        <span className="text-gray-500 line-through">${price}</span>
                                        <span className="text-gray-700 font-semibold">${specialPrice}</span>
                                    </div>
                                    ) : (
                                    <div className="flex flex-col">
                                        <span className="text-gray-700">${price}</span>
                                        {console.log(specialPrice)}
                                    </div>
                                    )}
                                </div>
                                
                            </div>

                            {isAvailable ? (
                                  <Status text={`In Stock: ${quantity} items available`} icon={MdDone} bg="bg-green-100" color="text-green-600" />
                                ) : (
                                  <Status text="Out of Stock" icon={MdClose} bg="bg-red-100" color="text-red-600" />
                            )}
                        
                        <Divider className="my-4"/>
                    
                    <p className="mt-2 text-sm/6 text-slate-800">
                        {description}
                    </p>
                      <div className="mt-4">
                    <Button
                    className="inline-flex items-center gap-2 rounded-md bg-gray-700 px-3 py-1.5 text-sm/6 font-semibold text-white shadow-inner shadow-white/10 focus:not-data-focus:outline-none data-focus:outline data-focus:outline-white data-hover:bg-gray-600 data-open:bg-gray-700"
                    onClick={() => setOpen(false)}
                    >
                    Close
                    </Button>
                    </div>
                </div>

              
            
            </DialogPanel>
          </div>
        </div>
      </Dialog>
    </>
  )
}

export default ProductViewModal;
