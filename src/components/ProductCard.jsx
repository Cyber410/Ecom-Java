import { useState } from "react";
import { FaShoppingCart } from "react-icons/fa";
import ProductViewModal from "./ProductViewModal";
const ProductCard = ({
    productId,
  productName,
  price,
  specialPrice,
  description,
  image,
  quantity,
  category
}) => {
  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const buttonLoader = false;
  const [selectedViewProduct, setSelectedViewProduct] = useState("");
  const isAvailable = quantity && Number(quantity) > 0;
  const handelProductView=(product)=>{
    setSelectedViewProduct(product);
    setOpenProductViewModal(true);
  }
  return (
    <div  onClick={() => { 
        handelProductView({
            productId,
          productName,
          price,
          specialPrice,
          description,
          image,
          quantity,
          category
        })
      }}className="boarder rounded-lg shadow-xl transition-shadow duration-300">
      <div  className=" w-full overflow-hidden aspect-[3/2]">
        <img src={image} alt={productName} className=" w-full h-full transition-transform duration-300 transform hover:scale-105 cursor-pointer"></img>
      </div>
     
        <div  className="p-4">

        <h2 onClick={()=>{}} className="text-lg font-semibold cursor-pointer">
            {productName}
        </h2>

        <div className="min-h-20 max-h-20">
        <p className="text-gray-600 text-sm">{description}</p>
        </div>

        <div className="flex flex-row justify-between">
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
            <button
                disabled={!isAvailable||buttonLoader}
                className={`bg-blue-500 ${isAvailable ? "opacity-100 hover:bg-blue-600" : "opacity-50"}
                text-white py-2 px-3 rounded-lg items-center transition-colors duration-300 w-36 flex justify-center`}   
            >
                <FaShoppingCart className="mr-2"/>
                {isAvailable ? "Add to Cart" : "Out of Stock"}
            </button>
        </div>
        </div>

      
        <ProductViewModal 
        open={openProductViewModal}
        setOpen={setOpenProductViewModal}
        product={selectedViewProduct}
         isAvailable={isAvailable}       
        />
    </div>
  );
};

export default ProductCard;