// package com.ecommerce.ecommerce.service.impl;

// import com.ecommerce.ecommerce.model.Address;
// import com.ecommerce.ecommerce.model.User;
// import com.ecommerce.ecommerce.repository.AddressRepository;
// import com.ecommerce.ecommerce.service.AddressService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Optional;

// @Service
// public class AddressServiceImpl implements AddressService {

//     @Autowired
//     private AddressRepository addressRepository;

//     // ✅ Save a new address
//     @Override
//     public Address saveAddress(Address address, User user) {
//         address.setUser(user);
//         return addressRepository.save(address);
//     }

//     // ✅ Get all addresses by user
//     @Override
//     public List<Address> getAddressesByUser(User user) {
//         return addressRepository.findByUser(user);
//     }

//     // ✅ Get address by ID
//     @Override
//     public Optional<Address> getAddressById(Long id) {
//         return addressRepository.findById(id);
//     }

//     // ✅ Update existing address
//     @Override
//     public Address updateAddress(Long id, Address updatedAddress, User user) {
//         Optional<Address> existing = addressRepository.findById(id);

//         if (existing.isPresent() && existing.get().getUser().getId().equals(user.getId())) {
//             Address address = existing.get();
//             address.setName(updatedAddress.getName());
//             address.setLocality(updatedAddress.getLocality());
//             address.setAddress(updatedAddress.getAddress());
//             address.setCity(updatedAddress.getCity());
//             address.setState(updatedAddress.getState());
//             address.setPincode(updatedAddress.getPincode());
//             address.setMobile(updatedAddress.getMobile());
//             return addressRepository.save(address);
//         } else {
//             throw new RuntimeException("Address not found or access denied");
//         }
//     }

//     // ✅ Delete address
//     @Override
//     public void deleteAddress(Long id, User user) {
//         Optional<Address> address = addressRepository.findById(id);
//         if (address.isPresent() && address.get().getUser().getId().equals(user.getId())) {
//             addressRepository.deleteById(id);
//         } else {
//             throw new RuntimeException("Address not found or access denied");
//         }
//     }
// }
