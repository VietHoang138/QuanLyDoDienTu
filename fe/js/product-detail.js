
const colorButtons = document.querySelectorAll('.variation-group:first-child .variation-option');
const mainImage = document.getElementById('main-product-image');

colorButtons.forEach(button => {
  button.addEventListener('click', () => {
    // Cập nhật ảnh chính
    const newImage = button.getAttribute('data-image');
    if (newImage) {
      mainImage.src = newImage;
    }

    // Cập nhật class 'selected'
    colorButtons.forEach(btn => btn.classList.remove('selected'));
    button.classList.add('selected');
  });
});

  // Chọn dung lượng
  const storageButtons = document.querySelectorAll('.variation-group:nth-child(2) .variation-option');
  storageButtons.forEach(button => {
    button.addEventListener('click', () => {
      storageButtons.forEach(btn => btn.classList.remove('selected'));
      button.classList.add('selected');
    });
  });

  const quantityInput = document.querySelector('#product-quantity-input');
  const increaseBtn = document.querySelector('.increase-quantity');
  const decreaseBtn = document.querySelector('.decrease-quantity');

  increaseBtn.addEventListener('click', () => {
    let currentValue = parseInt(quantityInput.value);
    if (currentValue < parseInt(quantityInput.max)) {
      quantityInput.value = currentValue + 1;
    }
  });

  decreaseBtn.addEventListener('click', () => {
    let currentValue = parseInt(quantityInput.value);
    if (currentValue > parseInt(quantityInput.min)) {
      quantityInput.value = currentValue - 1;
    }
  });

