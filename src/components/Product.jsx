import { FaExclamationTriangle } from "react-icons/fa";
import ProductCard from './ProductCard';

const Products= ()=>{
    const isLoading= false;
    const errorMessage="";
    const product =[
  {
    "productId": 1,
    "productName": "Apple iPhone 13",
    "price": 999.99,
    "description": "A high-end smartphone with a powerful A15 Bionic chip and a 6.1-inch Super Retina HD display",
    "image": "https://placehold.co/600x400",
    "category": "Electronics",
    "quantity": 0,
    "specialPrice": 899.99
  },
  {
    "productId": 2,
    "productName": "Samsung Galaxy S22",
    "price": 899.99,
    "description": "A high-end smartphone with a large 6.2-inch Dynamic AMOLED display and a long-lasting 4500mAh battery",
    "image": "https://placehold.co/600x400",
    "category": "Electronics",
    "quantity": 30,
    "specialPrice": 0
  },
  {
    "productId": 3,
    "productName": "Sony WH-1000XM4 Headphones",
    "price": 349.99,
    "description": "Wireless noise-cancelling headphones with industry-leading sound quality and up to 30 hours of battery life",
    "image": "https://placehold.co/600x400",
    "category": "Electronics",
    "quantity": 20,
    "specialPrice": 299.99
  }
]
    return (
        <div className="lg:px-14 sm:px-8 px-4 py-14 2xl:w-[90%] 2xl:mx-auto">
  {isLoading?
    (<div>Loading...</div>
    ):errorMessage?(
      <div className="flex justify-center items-center h:[200px]">
        <FaExclamationTriangle className="text slate-800 text-3xl mr-2"/>
        <span className="text-slate-800 text-lg font-medium">{errorMessage}</span>
        </div>
        
    ):( 
      <div className="min-h-[700px] ">
        <div className="pb-6 pt-14 grid 2xl:grid-cols-4 lg:grid-cols-3 sm:grid-cols-2  gap-x-6 gap-y-6">
          {product.map((item)=>(
            <div key={item.productId} className="border p-4 rounded-lg hover:shadow-lg duration-300 cursor-pointer">
              <div><ProductCard productId={item.productId} productName={item.productName}  price={item.price} description={item.description} image={item.image} quantity={item.quantity} category={item.category} specialPrice={item.specialPrice}/></div>
            </div> // --- added closing </div> tag here
          ))}
        </div>
      </div>
    )}
</div>
    )
};
export default Products;